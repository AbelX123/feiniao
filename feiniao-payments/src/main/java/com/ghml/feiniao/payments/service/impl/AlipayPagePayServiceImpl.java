package com.ghml.feiniao.payments.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.entity.OrderRecordEntity;
import com.ghml.feiniao.common.entity.PaymentRecordEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.OrderRecordMapper;
import com.ghml.feiniao.common.mapper.PaymentRecordMapper;
import com.ghml.feiniao.common.utils.SnowflakeIdGenerator;
import com.ghml.feiniao.payments.config.AlipayProperties;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import com.ghml.feiniao.payments.service.AlipayPagePayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayPagePayServiceImpl implements AlipayPagePayService {

    private static final String DEFAULT_PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    private static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String TRADE_FINISHED = "TRADE_FINISHED";
    private static final String TRADE_CLOSED = "TRADE_CLOSED";
    private static final String NOTIFY_SUCCESS = "success";
    private static final String NOTIFY_FAILURE = "failure";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AlipayProperties alipayProperties;
    private final OrderRecordMapper orderRecordMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String pagePay(AlipayPagePayDto alipayPagePayDto) throws AlipayApiException {
        String orderId = alipayPagePayDto.getOrderId();
        log.info("收到下单支付请求: orderId={}", orderId);

        OrderRecordEntity record = orderRecordMapper.selectById(orderId);
        if (record == null) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        if (record.getOrderStatus() == 1) {
            throw new ServiceException(Code.ORDER_ALREADY_PAY);
        }
        if (record.getOrderStatus() == 2) {
            throw new ServiceException(Code.ORDER_ALREADY_CANCEL);
        }

        PaymentRecordEntity paymentRecord = paymentRecordMapper.selectByOrderId(orderId);
        if (paymentRecord == null) {
            long outTradeNo = SnowflakeIdGenerator.nextId();
            paymentRecord = new PaymentRecordEntity();
            paymentRecord.setOutTradeNo(outTradeNo);
            paymentRecord.setOrderId(orderId);
            paymentRecord.setTradeStatus(WAIT_BUYER_PAY);
            paymentRecord.setTotalAmount(alipayPagePayDto.getTotalAmount());
            paymentRecord.setSubject(alipayPagePayDto.getSubject());
            paymentRecord.setCreateTime(LocalDateTime.now());
            paymentRecord.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.insert(paymentRecord);
            log.info("创建初始支付记录: orderId={}, outTradeNo={}", orderId, outTradeNo);
        }

        String alipayOutTradeNo = String.valueOf(paymentRecord.getOutTradeNo());

        if (paymentRecord.getTradeNo() != null && !paymentRecord.getTradeNo().isBlank()) {
            log.info("支付记录已存在支付宝交易号，主动查询订单状态: orderId={}, tradeNo={}",
                    orderId, paymentRecord.getTradeNo());
            String queryTradeStatus = queryAlipayTradeStatus(paymentRecord, orderId);
            if (TRADE_SUCCESS.equals(queryTradeStatus) || TRADE_FINISHED.equals(queryTradeStatus)) {
                orderRecordMapper.markOrderPaid(orderId);
                throw new ServiceException(Code.ORDER_ALREADY_PAY);
            }
            if (TRADE_CLOSED.equals(queryTradeStatus)) {
                orderRecordMapper.markOrderClosed(orderId);
                throw new ServiceException(Code.ORDER_ALREADY_CANCEL);
            }
        }

        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayProperties.getGatewayUrl(),
                alipayProperties.getAppId(),
                alipayProperties.getMerchantPrivateKey(),
                alipayProperties.getFormat(),
                alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getSignType()
        );

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayProperties.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(alipayOutTradeNo);
        model.setTotalAmount(alipayPagePayDto.getTotalAmount().toPlainString());
        model.setSubject(alipayPagePayDto.getSubject());
        model.setBody(alipayPagePayDto.getDesc());
        model.setTimeExpire(calculateTimeExpire(alipayPagePayDto.getOrderTime()));
        model.setProductCode(DEFAULT_PRODUCT_CODE);
        alipayRequest.setBizModel(model);

        try {
            String form = alipayClient.pageExecute(alipayRequest, "GET").getBody();
            log.info("请求下单支付结果: orderId={}, outTradeNo={}", orderId, alipayOutTradeNo);
            return form;
        } catch (AlipayApiException e) {
            log.error("调用支付宝下单接口异常: orderId={}", orderId, e);
            throw new AlipayApiException("alipay api exception");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notifyPay(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        String callbackOutTradeNo = params.get("out_trade_no");
        log.info("收到支付宝异步回调: outTradeNo={}, tradeStatus={}",
                callbackOutTradeNo, params.get("trade_status"));
        try {
            // 1. 验签
            boolean signValid = AlipaySignature.rsaCheckV1(
                    params,
                    alipayProperties.getAlipayPublicKey(),
                    alipayProperties.getCharset(),
                    alipayProperties.getSignType()
            );
            if (!signValid) {
                log.warn("支付宝异步回调验签失败: outTradeNo={}", callbackOutTradeNo);
                return NOTIFY_FAILURE;
            }

            // 2. 校验 app_id
            String appId = params.get("app_id");
            if (!alipayProperties.getAppId().equals(appId)) {
                log.warn("支付宝异步回调 app_id 不匹配: expected={}, actual={}",
                        alipayProperties.getAppId(), appId);
                return NOTIFY_FAILURE;
            }

            // 3. 校验 seller_id（如果配置了）
            String sellerId = alipayProperties.getSellerId();
            if (sellerId != null && !sellerId.isBlank()
                    && !sellerId.equals(params.get("seller_id"))) {
                log.warn("支付宝异步回调 seller_id 不匹配: expected={}, actual={}",
                        sellerId, params.get("seller_id"));
                return NOTIFY_FAILURE;
            }

            // 4. 回调的 out_trade_no 就是我们的主键，按主键查支付记录
            if (callbackOutTradeNo == null || callbackOutTradeNo.isBlank()) {
                log.warn("支付宝异步回调缺少 out_trade_no");
                return NOTIFY_FAILURE;
            }

            Long outTradeNo;
            try {
                outTradeNo = Long.parseLong(callbackOutTradeNo);
            } catch (NumberFormatException e) {
                log.warn("支付宝异步回调 out_trade_no 格式异常: {}", callbackOutTradeNo);
                return NOTIFY_FAILURE;
            }

            PaymentRecordEntity existed = paymentRecordMapper.selectById(outTradeNo);
            if (existed == null) {
                log.error("支付宝异步回调找不到本地支付记录: outTradeNo={}", outTradeNo);
                return NOTIFY_FAILURE;
            }

            // 5. 校验金额一致性
            BigDecimal notifyAmount = parseBigDecimal(params.get("total_amount"));
            if (notifyAmount == null || existed.getTotalAmount().compareTo(notifyAmount) != 0) {
                log.warn("支付宝异步回调金额不一致: outTradeNo={}, local={}, notify={}",
                        outTradeNo, existed.getTotalAmount(), notifyAmount);
                return NOTIFY_FAILURE;
            }

            // 6. 幂等：已处于终态的记录不再重复处理
            String existedStatus = existed.getTradeStatus();
            if (TRADE_SUCCESS.equals(existedStatus)
                    || TRADE_FINISHED.equals(existedStatus)
                    || TRADE_CLOSED.equals(existedStatus)) {
                log.info("支付记录已处于终态，跳过处理: outTradeNo={}, status={}",
                        outTradeNo, existedStatus);
                return NOTIFY_SUCCESS;
            }

            // 7. 更新支付记录
            existed.setTradeNo(params.get("trade_no"));
            existed.setTradeStatus(params.get("trade_status"));
            existed.setReceiptAmount(parseBigDecimal(params.get("receipt_amount")));
            existed.setBuyerPayAmount(parseBigDecimal(params.get("buyer_pay_amount")));
            existed.setPointAmount(parseBigDecimal(params.get("point_amount")));
            existed.setGmtPayment(parseDateTime(params.get("gmt_payment")));
            existed.setGmtClose(parseDateTime(params.get("gmt_close")));
            existed.setFundBillList(params.get("fund_bill_list"));
            existed.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(existed);

            // 8. 通过支付记录的 order_id 联动更新订单状态
            String orderId = existed.getOrderId();
            String tradeStatus = params.get("trade_status");
            if (TRADE_SUCCESS.equals(tradeStatus) || TRADE_FINISHED.equals(tradeStatus)) {
                orderRecordMapper.markOrderPaid(orderId);
            } else if (TRADE_CLOSED.equals(tradeStatus)) {
                orderRecordMapper.markOrderClosed(orderId);
            }
            return NOTIFY_SUCCESS;
        } catch (Exception e) {
            log.error("处理支付宝异步回调失败: outTradeNo={}, reason={}",
                    callbackOutTradeNo, e.getMessage(), e);
            return NOTIFY_FAILURE;
        }
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        requestParams.forEach((key, values) -> {
            if (values == null || values.length == 0) {
                params.put(key, "");
            } else if (values.length == 1) {
                params.put(key, values[0]);
            } else {
                params.put(key, String.join(",", Arrays.asList(values)));
            }
        });
        return params;
    }

    private String calculateTimeExpire(String orderTime) {
        int payTimeout = alipayProperties.getPayTimeout();
        if (orderTime == null || orderTime.trim().isEmpty()) {
            return LocalDateTime.now().plusMinutes(payTimeout).format(DATE_FORMATTER);
        }

        try {
            LocalDateTime orderDateTime = LocalDateTime.parse(orderTime, DATE_FORMATTER);
            return orderDateTime.plusMinutes(payTimeout).format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("解析订单时间失败: {}", orderTime, e);
            return LocalDateTime.now().plusMinutes(payTimeout).format(DATE_FORMATTER);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("时间字段解析失败: {}", value);
            return null;
        }
    }

    private String queryAlipayTradeStatus(PaymentRecordEntity paymentRecord, String orderId) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayProperties.getGatewayUrl(),
                    alipayProperties.getAppId(),
                    alipayProperties.getMerchantPrivateKey(),
                    alipayProperties.getFormat(),
                    alipayProperties.getCharset(),
                    alipayProperties.getAlipayPublicKey(),
                    alipayProperties.getSignType()
            );

            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
            queryModel.setTradeNo(paymentRecord.getTradeNo());
            queryModel.setOutTradeNo(String.valueOf(paymentRecord.getOutTradeNo()));
            queryRequest.setBizModel(queryModel);

            AlipayTradeQueryResponse response = alipayClient.execute(queryRequest);
            if (!response.isSuccess()) {
                log.warn("支付宝交易查询失败: orderId={}, code={}, subCode={}, subMsg={}",
                        orderId, response.getCode(), response.getSubCode(), response.getSubMsg());
                return null;
            }

            String tradeStatus = response.getTradeStatus();
            log.info("支付宝交易查询成功: orderId={}, tradeStatus={}", orderId, tradeStatus);

            paymentRecord.setTradeStatus(tradeStatus);
            paymentRecord.setReceiptAmount(parseBigDecimal(response.getReceiptAmount()));
            paymentRecord.setBuyerPayAmount(parseBigDecimal(response.getBuyerPayAmount()));
            paymentRecord.setPointAmount(parseBigDecimal(response.getPointAmount()));
            Date sendPayDate = response.getSendPayDate();
            if (sendPayDate != null) {
                paymentRecord.setGmtPayment(
                        sendPayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                );
            }
            paymentRecord.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(paymentRecord);

            return tradeStatus;
        } catch (AlipayApiException e) {
            log.error("查询支付宝交易状态异常: orderId={}, tradeNo={}",
                    orderId, paymentRecord.getTradeNo(), e);
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("金额字段解析失败: {}", value);
            return null;
        }
    }
}

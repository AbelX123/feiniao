package com.ghml.feiniao.payments.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ghml.feiniao.payments.config.AlipayProperties;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
public class AlipayPagePayService {

    private static final String DEFAULT_PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";

    private final AlipayProperties alipayProperties;

    public AlipayPagePayService(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
    }

    public String pagePay(AlipayPagePayDto alipayPagePayDto) throws AlipayApiException {

        // 初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayProperties.getGatewayUrl(), // 支付宝网关
                alipayProperties.getAppId(), // 应用id
                alipayProperties.getMerchantPrivateKey(), // 应用私钥
                alipayProperties.getFormat(), // 报文格式
                alipayProperties.getCharset(), // 编码格式
                alipayProperties.getAlipayPublicKey(), // 支付宝公钥
                alipayProperties.getSignType()); // 签名类型

        // 实例化具体api对应的request类，类名称和接口名称对应
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        /**
         * 同步通知地址，商户外网可以post访问的异步地址，用于接收支付宝返回的支付结果
         * 注意 支付结果以异步通知为准，不能以同步返回为准
         */
        alipayRequest.setReturnUrl(alipayProperties.getReturnUrl());

        /**
         * 异步通知地址，商户外网可以post访问的异步地址，用于接收支付宝返回的支付结果
         */
        alipayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());

        // 设置业务参数
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        // 商户订单号
        model.setOutTradeNo(alipayPagePayDto.getOutTradeNo());

        // 订单金额
        model.setTotalAmount(alipayPagePayDto.getTotalAmount().toPlainString());

        // 订单标题
        model.setSubject(alipayPagePayDto.getSubject());

        // 订单描述
        model.setBody(alipayPagePayDto.getDesc());

        // 绝对超时时间，下单时间+半个小时
        model.setTimeExpire(calculateTimeExpire(alipayPagePayDto.getOrderTime()));

        // 销售产品码，固定值
        model.setProductCode(DEFAULT_PRODUCT_CODE);

        // 将业务参数传至request中
        alipayRequest.setBizModel(model);

        String form;

        try {
            // 调用SDK生成form表单
//            form = alipayClient.pageExecute(alipayRequest).getBody();

            // 调用SDK生成支付链接，可在浏览器打开链接进入支付页面
            form = alipayClient.pageExecute(alipayRequest, "GET").getBody();

        } catch (AlipayApiException e) {
            log.error("alipay api exception", e);
            throw new AlipayApiException("alipay api exception");
        }

        return form;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String calculateTimeExpire(String orderTime) {
        int payTimeout = alipayProperties.getPayTimeout();
        if (orderTime == null || orderTime.trim().isEmpty()) {
            // 降级：使用当前时间
            return LocalDateTime.now().plusMinutes(payTimeout).format(DATE_FORMATTER);
        }

        try {
            LocalDateTime orderDateTime = LocalDateTime.parse(orderTime, DATE_FORMATTER);
            LocalDateTime expireTime = orderDateTime.plusMinutes(payTimeout);
            return expireTime.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 日志记录错误
            log.error("解析订单时间失败: {}", orderTime, e);
            // 降级：使用当前时间
            return LocalDateTime.now().plusMinutes(payTimeout).format(DATE_FORMATTER);
        }
    }
}

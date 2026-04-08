package com.ghml.feiniao.payments.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * alipay.trade.page.pay 请求参数。
 */
@Data
public class AlipayPagePayDto {

    private String outTradeNo; // 商户订单号
    private BigDecimal totalAmount; // 订单金额
    private String subject; // 订单标题
    private String desc; // 订单描述
    private String orderTime; // 下单时间
}

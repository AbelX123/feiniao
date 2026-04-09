package com.ghml.feiniao.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private String appId; // 应用id
    private String sellerId; // 商户PID（seller_id），用于回调校验，可选
    private String merchantPrivateKey; // 应用私钥
    private String alipayPublicKey; // 支付宝公钥
    private String notifyUrl; // 异步通知地址
    private String returnUrl; // 同步通知地址
    private String format = "JSON"; // 报文格式，仅支持json格式
    private String signType = "RSA2"; // 签名类型
    private String charset = "UTF-8"; // 编码格式
    private String gatewayUrl = "https://openapi.alipay.com/gateway.do"; // 支付宝网关
    private int payTimeout = 30; // 未支付超时时间单位m

}

package com.ghml.feiniao.payments.sdk.demo;

import com.alipay.api.CertAlipayRequest;
import lombok.Data;

@Data
public class AlipayConfig extends CertAlipayRequest {
    private String serverUrl;
    private String appId;
    private String privateKey;
    private String format;
    private String alipayPublicKey;
    private String charset;
    private String signType;
}

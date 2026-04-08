package com.ghml.feiniao.payments.controller;

import com.alipay.api.AlipayApiException;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import com.ghml.feiniao.payments.service.AlipayPagePayService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝
 */
@RestController
@RequestMapping("/api/alipay")
public class AlipayController {

    private final AlipayPagePayService alipayPagePayService;

    public AlipayController(AlipayPagePayService alipayPagePayService) {
        this.alipayPagePayService = alipayPagePayService;
    }

    // 统一收单下单并支付页面接口
    @PostMapping(value = "/trade/page/pay", produces = MediaType.TEXT_HTML_VALUE)
    public String tradePagePay(@RequestBody AlipayPagePayDto alipayPagePayDto) throws AlipayApiException {
        return alipayPagePayService.pagePay(alipayPagePayDto);
    }
}

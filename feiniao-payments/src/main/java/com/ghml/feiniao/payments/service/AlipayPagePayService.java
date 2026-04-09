package com.ghml.feiniao.payments.service;

import com.alipay.api.AlipayApiException;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AlipayPagePayService {

    String pagePay(AlipayPagePayDto alipayPagePayDto) throws AlipayApiException;

    String notifyPay(HttpServletRequest request);
}

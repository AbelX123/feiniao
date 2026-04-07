package com.ghml.feiniao.payments.controller;

import com.alipay.api.AlipayApiException;
import com.ghml.feiniao.payments.model.CreateOrderRequest;
import com.ghml.feiniao.payments.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/createOrder", produces = MediaType.TEXT_HTML_VALUE)
    public String createOrder(@RequestBody CreateOrderRequest request) throws AlipayApiException {
        return paymentService.createOrder(request);
    }

    @GetMapping("/demo")
    public void createDemo(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException {
        paymentService.demo(request, response);
    }
}

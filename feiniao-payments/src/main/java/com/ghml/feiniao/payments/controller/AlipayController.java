package com.ghml.feiniao.payments.controller;

import com.alipay.api.AlipayApiException;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import com.ghml.feiniao.payments.service.AlipayPagePayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝
 */
@Slf4j
@RestController
@RequestMapping("/api/alipay")
public class AlipayController {

    private final AlipayPagePayService alipayPagePayService;

    public AlipayController(AlipayPagePayService alipayPagePayService) {
        this.alipayPagePayService = alipayPagePayService;
    }

    // 统一收单下单并支付页面接口
    @PostMapping("/trade/page/pay")
    public String tradePagePay(@RequestBody AlipayPagePayDto alipayPagePayDto) throws AlipayApiException {
        return alipayPagePayService.pagePay(alipayPagePayDto);
    }

//    // 同步回调 - GET请求
//    @GetMapping("/return")
//    public String returnUrl(HttpServletRequest request) {
//        // 打印所有参数看看
//        Map<String, String[]> params = request.getParameterMap();
//        System.out.println("同步回调参数: " + params);
//        params.forEach((k, v) -> {
//            System.out.println(k + ":" + Arrays.toString(v));
//        });
//        return "支付成功，请返回商家";
//    }

    // 异步回调 - POST请求
    @PostMapping("/notify")
    public String notifyUrl(HttpServletRequest request) {
        return alipayPagePayService.notifyPay(request);
    }

}

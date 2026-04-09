package com.ghml.feiniao.payments.controller;

import com.alipay.api.AlipayApiException;
import com.ghml.feiniao.payments.dto.AlipayPagePayDto;
import com.ghml.feiniao.payments.service.AlipayPagePayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

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
        // 打印所有参数看看
        Map<String, String[]> params = request.getParameterMap();
        System.out.println("异步回调参数: " + params);
        params.forEach((k, v) -> {
            System.out.println(k + ":" + Arrays.toString(v));
        });
        // 支付宝要求必须返回 success
        return "success";
    }

    /**
     异步回调参数: org.apache.catalina.util.ParameterMap@1d2c4239
     gmt_create:[2026-04-08 19:42:26]
     charset:[utf-8]
     subject:[苹果电脑]
     sign:[QffMEz4A+3G5kouhXTPBoGekYOoYIBfrIscucVS3s4vZmWaj34YmrPsp/gXoNVXnr4SrS9x840jKc56OkVe707lsyd2Vje69uQF/rk3izcOq/H1JBk1jGU4+l9gmyW9igTrBMa+ju1cLvbjTys+VlKCtMyv3ycvrw0reWzbbsplvtIofHQuimBi47Mcywxg/QyKbhwzQjgVZspk4RSxDUeAGTb2pHGN+e9UmuU6UfhgVuHBjRj1tJpkOFdsUOpycBcXQmgC6LQHSX7MLgRW4DPCmhK7mW4CesISJpGVGdJjDhDFoyRhMwl7zkyr+WROQZmAxkcMvW2ylgnuMpnyWrA==]
     body:[好]
     invoice_amount:[0.01]
     buyer_open_id:[0245RF80ecSAJ9WlbEd5Sn-3PLxMuzA2iO-xyixFmccfmcb]
     notify_id:[2026040801222194235043241401864041]
     fund_bill_list:[[{"amount":"0.01","fundChannel":"ALIPAYACCOUNT"}]]
     notify_type:[trade_status_sync]
     trade_status:[TRADE_SUCCESS]
     receipt_amount:[0.01]
     buyer_pay_amount:[0.01]
     app_id:[2021006136631689]
     sign_type:[RSA2]
     seller_id:[2088380808229000]
     gmt_payment:[2026-04-08 19:42:34]
     notify_time:[2026-04-08 19:42:35]
     merchant_app_id:[2021006136631689]
     version:[1.0]
     out_trade_no:[007d2429c89a4860b13aaa2e844ee940]
     total_amount:[0.01]
     trade_no:[2026040822001443241452693463]
     auth_app_id:[2021006136631689]
     point_amount:[0.00]
     */

}

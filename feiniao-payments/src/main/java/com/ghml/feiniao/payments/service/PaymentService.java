package com.ghml.feiniao.payments.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.ghml.feiniao.payments.config.AlipayProperties;
import com.ghml.feiniao.payments.model.CreateOrderRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Service
public class PaymentService {

    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";

    private final AlipayProperties alipayProperties;

    public PaymentService(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
    }

    public String createOrder(CreateOrderRequest request) throws AlipayApiException {
        validateRequest(request);

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(alipayProperties.getGatewayUrl());
        alipayConfig.setAppId(alipayProperties.getAppId());
        alipayConfig.setPrivateKey(alipayProperties.getMerchantPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayProperties.getAlipayPublicKey());
        alipayConfig.setCharset(alipayProperties.getCharset());
        alipayConfig.setSignType(alipayProperties.getSignType());

        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        payRequest.setNotifyUrl(alipayProperties.getNotifyUrl());
        payRequest.setReturnUrl(alipayProperties.getReturnUrl());

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(request.getOutTradeNo());
        model.setTotalAmount(request.getTotalAmount().toPlainString());
        model.setSubject(request.getSubject());
        model.setBody(request.getBody());
        model.setProductCode(PRODUCT_CODE);
        model.setTimeoutExpress(
                StringUtils.hasText(request.getTimeoutExpress())
                        ? request.getTimeoutExpress()
                        : alipayProperties.getTimeout()
        );
        payRequest.setBizModel(model);

        AlipayTradePagePayResponse response = alipayClient.pageExecute(payRequest, "POST");
        return response.getBody();
    }

    private void validateRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (!StringUtils.hasText(request.getOutTradeNo())) {
            throw new IllegalArgumentException("outTradeNo 不能为空");
        }
        if (request.getTotalAmount() == null || request.getTotalAmount().doubleValue() <= 0D) {
            throw new IllegalArgumentException("totalAmount 必须大于 0");
        }
        if (!StringUtils.hasText(request.getSubject())) {
            throw new IllegalArgumentException("subject 不能为空");
        }
    }

    public void demo(HttpServletRequest request, HttpServletResponse response) {
        try {
            doPost(request, response);
        } catch (ServletException | IOException e) {
            log.info(e.getLocalizedMessage());
        }
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        /** 支付宝网关 **/
        String URL = "https://openapi.alipay.com/gateway.do";

        /** 应用id，如何获取请参考：https://opensupport.alipay.com/support/helpcenter/190/201602493024 **/
        String APP_ID = "2021006136631689";

        /** 应用私钥，如何获取请参考：https://opensupport.alipay.com/support/helpcenter/207/201602471154?ant_source=antsupport **/
        String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAaBqJlgoAK+uJxjCYwJz4eQdmoqEVDEwnj/pDfC6JU6gDcke2aO9FJ3kaLifOKkqsId9kl9qI85s7UJ2bjlZRVXap7gq8mpmjIFo9IsbQ8KBVdgMWCvWRDd49W6wmFhOoGrQtKAHLJtI56lBnMFkC2jSXVRB3CshnI2bpIHJiaNwT9k4Ye1pLPZeWi/TnU17kjYrJMZeJtZ/YeSSJU/k91C/p5TmX/gRhH7SrSgJ6HNQe4GQP+omQ8aYHGABR1Zv7Q0oChS8VcqQb3x6iW7QwRAiF25xBKKPKXVZhKMOcmSBkMfTINHQF/3JOtcUdqj0O5vypKcGxt5nPnQmBcS3ZAgMBAAECggEAUv5UvBZRGAPZ5KLtoWYsD229SINL8lVMoCAVtnhhZnEEXwAzeLZIx06giS9xkhdiAF/YCX8LD9nskqhSY6ABEbJIrkH0tagGL3wsEITot4Wj01lFOYPeqwQNYfAiEIejUPa702KXa3bCZaLxraEINBfFaaWSpZasHPkDUfnh/Ro4JoSVx66JC8HK7HJ5capAoY4lC/QDr7Pq/VcU3P7t4mXm5tr1yWmOuCjySnOn3jz2ZgiWVjlL6mJm8n0UXuGFTkZCONrPUgo/PpG6/IRQDWS3n9kYIXjHiwcWfAva21WfubhQijbXtR7VhojboBggRSkvvqsvf0nCr18+kJ1K1QKBgQDudPY3Bjveg3MptqawyRp7zDizGBj9pPjDJp6xTNqtfBM79dlgdp8W+OdSoL6Dz+3/2HovzsMcY4MRBvVYDiM3353u/28TpfkN3GVmoRgf7o/PJrO037bR1OLUrv3tEliOyjj0XH86LdzEAAAQ2RYbG4vKXsalijAE2vYmPwZKawKBgQDOj9fe0FrOuGpU0uyJ9vDPELMJPpoz6LUxzvL7CYJjm2UiBcwI59qcbqTU8oUrwBTeja2R6YE6/ihS3dmDsC2Mi1Ohh2tW3ICX873XSBifPntxmCo/3o1U6tmt1T8kZDs7+/mK6ozBi+9Bgc76RSRxhZYIf8WlLu9aaIpTfW1BywKBgQCp4rRjoVJYBiivCE0b2DrBBk9C8SEk3DrKtiILSSjmvmXpqw7ylbBkKnKLwK/asvHp++2ZT+JbwF3pbJ6w3Gmk7fDUDDGzuuSERogLpSvN3CWivPz9GQFj3xq30fBgzVhAarxHA/s0lOYa0bE6T59bMrXWOM2ntJrfaqxBtVci/wKBgGdJIcX2JRHswK59cW1jHvJLBVsABJckK0YmlvTTwnEQCLfEckuZs4Q5Bw673b8MouLNsRaezdGzuHIHIHZqp7v5Bu5kClY6l8pjRG/bSwSFd7BLSAT7wWvOqt3zHUmTHNNXoWXG9m7pC8+Tmu0lmZ1yEJ1bVnbMSz7HuzMYKoOVAoGBAO0s0wyo2/kV8v+VKZ+gz3FF63/KmwSe7VdfNvn1tbJnSxPa+/096P8Plo3THoJnNlPMdi66iLUuQj5gUNVQ81N/E0UeUeCieAFvI3Ebe4PSq5mT4Ieo+9p66oMg18rwpjbfjRWVWNpiJWCc9fLCJ2a9zclCh5ZRJYTbRiWKZRvo";

        /** 支付宝公钥，如何获取请参考：https://opensupport.alipay.com/support/helpcenter/207/201602487431 **/
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl1E/PzFA5tY38HyN7bPNaomDJ3tKBtEcYhS7sYyCtFzgYDpcZ1kUzc7yfvvXGO2Jdg5RfAQM3Z8J+yvMaGqn14gjDjrZcIBwWKTFM9eevq8l9una6Qsu+hLVKV6ZNs5k9cyanDVeVb5+gLE2aR85XDYxEyHwOF/gyHlrx2Ktf1wra2PiM+QazKeiO5iI4H20Th24hlK8ira9bnUs5+KwXRse6olbmEKc7zsee8J/IqVnYYDABJG9JK+JzLyM8nrNqoNO27qjegSEsExTAr/3tvBTDRqOOr3UCmEEzbDnzbBjsvZOqPR95UaJEiVEz3z5ii0PUGezKOYh4AU/O6F0VwIDAQAB";

        /** 初始化 **/
        AlipayClient alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");

        /** 实例化具体API对应的request类，类名称和接口名称对应,当前调用接口名称：alipay.trade.page.pay（电脑网站支付） **/
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        /** 设置业务参数  **/
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        /** 商户订单号,商户自定义，需保证在商户端不重复，如：20200612000001 **/
        model.setOutTradeNo("20200612000001");

        /** 销售产品码,固定值：FAST_INSTANT_TRADE_PAY **/
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        /** 订单标题 **/
        model.setSubject("订单标题");

        /** 订单金额，精确到小数点后两位 **/
        model.setTotalAmount("0.01");

        /** 订单描述 **/
        model.setBody("订单描述");

        /** 将业务参数传至request中 **/
        alipayRequest.setBizModel(model);

        /** 注：支付结果以异步通知为准，不能以同步返回为准，因为如果实际支付成功，但因为外力因素，如断网、断电等导致页面没有跳转，则无法接收到同步通知；**/
        /** 同步通知地址，以http或者https开头，支付完成后跳转的地址，用于用户视觉感知支付已成功，传值外网可以访问的地址，如果同步未跳转可参考该文档进行确认：https://opensupport.alipay.com/support/helpcenter/193/201602474937 **/
        alipayRequest.setReturnUrl("");

        /** 异步通知地址，以http或者https开头的，商户外网可以post访问的异步地址，用于接收支付宝返回的支付结果，如果未收到该通知可参考该文档进行确认：https://opensupport.alipay.com/support/helpcenter/193/201602475759 **/
        alipayRequest.setNotifyUrl("");


        String form = null;
        try {

            /** 调用SDK生成表单form表单 **/
//            form = alipayClient.pageExecute(alipayRequest).getBody();

            /** 调用SDK生成支付链接，可在浏览器打开链接进入支付页面 **/
            form = alipayClient.pageExecute(alipayRequest, "GET").getBody();

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        /** 获取接口调用结果，如果调用失败，可根据返回错误信息到该文档寻找排查方案：https://opensupport.alipay.com/support/helpcenter/93 **/
        System.out.println(form);

    }
}

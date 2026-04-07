package com.ghml.feiniao.payments.sdk.demo;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;

import java.util.ArrayList;
import java.util.List;

public class AlipayTradePagePay {

    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        // 设置商户订单号
        model.setOutTradeNo("20150320010101001");

        // 设置订单总金额
        model.setTotalAmount("88.88");

        // 设置订单标题
        model.setSubject("Iphone6 16G");

        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        // 设置PC扫码支付的方式
        model.setQrPayMode("1");

        // 设置商户自定义二维码宽度
        model.setQrcodeWidth(100L);

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setAlipayGoodsId("20010001");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置订单绝对超时时间
        model.setTimeExpire("2016-12-31 10:05:01");

        // 设置二级商户信息
        SubMerchant subMerchant = new SubMerchant();
        subMerchant.setMerchantId("2088000603999128");
        subMerchant.setMerchantType("alipay");
        model.setSubMerchant(subMerchant);

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");

        // 设置优惠参数
        model.setPromoParams("{\"storeIdType\":\"1\"}");

        // 设置请求后页面的集成方式
        model.setIntegrationType("PCWEB");

        // 设置请求来源地址
        model.setRequestFromUrl("https://");

        // 设置商户门店编号
        model.setStoreId("NJ_001");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        // 设置外部指定买家
        ExtUserInfo extUserInfo = new ExtUserInfo();
        extUserInfo.setCertType("IDENTITY_CARD");
        extUserInfo.setCertNo("362334768769238881");
        extUserInfo.setMobile("16587658765");
        extUserInfo.setName("李明");
        extUserInfo.setMinAge("18");
        extUserInfo.setNeedCheckInfo("F");
        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
        model.setExtUserInfo(extUserInfo);

        // 设置开票信息
        InvoiceInfo invoiceInfo = new InvoiceInfo();
        InvoiceKeyInfo keyInfo = new InvoiceKeyInfo();
        keyInfo.setTaxNum("1464888883494");
        keyInfo.setIsSupportInvoice(true);
        keyInfo.setInvoiceMerchantName("ABC|003");
        invoiceInfo.setKeyInfo(keyInfo);
        invoiceInfo.setDetails("[{\"code\":\"100294400\",\"name\":\"服饰\",\"num\":\"2\",\"sumPrice\":\"200.00\",\"taxRate\":\"6%\"}]");
        model.setInvoiceInfo(invoiceInfo);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

//        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
         AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }

    private static AlipayConfig getAlipayConfig() {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAaBqJlgoAK+uJxjCYwJz4eQdmoqEVDEwnj/pDfC6JU6gDcke2aO9FJ3kaLifOKkqsId9kl9qI85s7UJ2bjlZRVXap7gq8mpmjIFo9IsbQ8KBVdgMWCvWRDd49W6wmFhOoGrQtKAHLJtI56lBnMFkC2jSXVRB3CshnI2bpIHJiaNwT9k4Ye1pLPZeWi/TnU17kjYrJMZeJtZ/YeSSJU/k91C/p5TmX/gRhH7SrSgJ6HNQe4GQP+omQ8aYHGABR1Zv7Q0oChS8VcqQb3x6iW7QwRAiF25xBKKPKXVZhKMOcmSBkMfTINHQF/3JOtcUdqj0O5vypKcGxt5nPnQmBcS3ZAgMBAAECggEAUv5UvBZRGAPZ5KLtoWYsD229SINL8lVMoCAVtnhhZnEEXwAzeLZIx06giS9xkhdiAF/YCX8LD9nskqhSY6ABEbJIrkH0tagGL3wsEITot4Wj01lFOYPeqwQNYfAiEIejUPa702KXa3bCZaLxraEINBfFaaWSpZasHPkDUfnh/Ro4JoSVx66JC8HK7HJ5capAoY4lC/QDr7Pq/VcU3P7t4mXm5tr1yWmOuCjySnOn3jz2ZgiWVjlL6mJm8n0UXuGFTkZCONrPUgo/PpG6/IRQDWS3n9kYIXjHiwcWfAva21WfubhQijbXtR7VhojboBggRSkvvqsvf0nCr18+kJ1K1QKBgQDudPY3Bjveg3MptqawyRp7zDizGBj9pPjDJp6xTNqtfBM79dlgdp8W+OdSoL6Dz+3/2HovzsMcY4MRBvVYDiM3353u/28TpfkN3GVmoRgf7o/PJrO037bR1OLUrv3tEliOyjj0XH86LdzEAAAQ2RYbG4vKXsalijAE2vYmPwZKawKBgQDOj9fe0FrOuGpU0uyJ9vDPELMJPpoz6LUxzvL7CYJjm2UiBcwI59qcbqTU8oUrwBTeja2R6YE6/ihS3dmDsC2Mi1Ohh2tW3ICX873XSBifPntxmCo/3o1U6tmt1T8kZDs7+/mK6ozBi+9Bgc76RSRxhZYIf8WlLu9aaIpTfW1BywKBgQCp4rRjoVJYBiivCE0b2DrBBk9C8SEk3DrKtiILSSjmvmXpqw7ylbBkKnKLwK/asvHp++2ZT+JbwF3pbJ6w3Gmk7fDUDDGzuuSERogLpSvN3CWivPz9GQFj3xq30fBgzVhAarxHA/s0lOYa0bE6T59bMrXWOM2ntJrfaqxBtVci/wKBgGdJIcX2JRHswK59cW1jHvJLBVsABJckK0YmlvTTwnEQCLfEckuZs4Q5Bw673b8MouLNsRaezdGzuHIHIHZqp7v5Bu5kClY6l8pjRG/bSwSFd7BLSAT7wWvOqt3zHUmTHNNXoWXG9m7pC8+Tmu0lmZ1yEJ1bVnbMSz7HuzMYKoOVAoGBAO0s0wyo2/kV8v+VKZ+gz3FF63/KmwSe7VdfNvn1tbJnSxPa+/096P8Plo3THoJnNlPMdi66iLUuQj5gUNVQ81N/E0UeUeCieAFvI3Ebe4PSq5mT4Ieo+9p66oMg18rwpjbfjRWVWNpiJWCc9fLCJ2a9zclCh5ZRJYTbRiWKZRvo";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl1E/PzFA5tY38HyN7bPNaomDJ3tKBtEcYhS7sYyCtFzgYDpcZ1kUzc7yfvvXGO2Jdg5RfAQM3Z8J+yvMaGqn14gjDjrZcIBwWKTFM9eevq8l9una6Qsu+hLVKV6ZNs5k9cyanDVeVb5+gLE2aR85XDYxEyHwOF/gyHlrx2Ktf1wra2PiM+QazKeiO5iI4H20Th24hlK8ira9bnUs5+KwXRse6olbmEKc7zsee8J/IqVnYYDABJG9JK+JzLyM8nrNqoNO27qjegSEsExTAr/3tvBTDRqOOr3UCmEEzbDnzbBjsvZOqPR95UaJEiVEz3z5ii0PUGezKOYh4AU/O6F0VwIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId("2021006136631689");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}

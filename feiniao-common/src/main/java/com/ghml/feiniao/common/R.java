package com.ghml.feiniao.common;

import lombok.Data;

@Data
public class R<T> {

    private String code;
    private String msg;
    private T data;

    // 成功-无数据
    public static <T> R<T> ok() {
        return restR(null, Code.SUCCESS);
    }

    // 成功-有数据
    public static <T> R<T> ok(T data) {
        return restR(data, Code.SUCCESS);
    }

    // 成功-自定义消息
    public static <T> R<T> ok(T data, String msg) {
        R<T> r = restR(data, Code.SUCCESS);
        r.setMsg(msg);
        return r;
    }

    // 失败-使用枚举
    public static <T> R<T> failed(Code code) {
        return restR(null, code);
    }

    // 失败-自定义消息
    public static <T> R<T> failed(String msg) {
        R<T> r = restR(null, Code.FAILED);
        r.setMsg(msg);
        return r;
    }

    // 内部构建函数
    private static <T> R<T> restR(T data, Code code) {
        R<T> r = new R<>();
        r.setCode(code.getCode());
        r.setMsg(code.getMsg());
        r.setData(data);
        return r;
    }
}

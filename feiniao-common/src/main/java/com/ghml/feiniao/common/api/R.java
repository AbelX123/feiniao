package com.ghml.feiniao.common.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // 转换为json字符串
    public String toJsonStr() {
        return JSON.toJSONString(this, JSONWriter.Feature.IgnoreNonFieldGetter);
    }
}

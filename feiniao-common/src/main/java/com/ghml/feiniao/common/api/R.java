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
        return restR(Code.SUCCESS, null);
    }

    // 成功-有数据
    public static <T> R<T> ok(T data) {
        return restR(Code.SUCCESS, data);
    }

    // 失败-无数据
    public static <T> R<T> failed(Code code) {
        return restR(code, null);
    }

    // 失败-有数据
    public static <T> R<T> failed(Code code, T data) {
        return restR(code, data);
    }

    // 内部构建函数
    private static <T> R<T> restR(Code code, T data) {
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

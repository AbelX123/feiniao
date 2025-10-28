package com.ghml.feiniao.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Getter
@AllArgsConstructor
public enum Code {
    SUCCESS("0000", "成功"),

    FAILED("9999", "系统错误"),

    _404("404", "访问错误的资源"),

    _401("401", "身份未验证"),

    _403("403", "拒绝访问"),

    PARAM_ERROR("9001", "参数错误"),

    // 用户相关错误码从1000开始
    USER_EXIST("1000", "用户已存在"),

    USER_NOT_EXIST("1001", "用户不存在"),

    USER_PASSWORD_NOT_MATCH("1002", "用户名或密码错误"),

    TOKEN_EXPIRED("1003", "令牌已过期"),

    TOKEN_INVALID("1004", "令牌无效");

    private final String code;
    private final String msg;

}

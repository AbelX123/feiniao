package com.ghml.feiniao.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Code {
    SUCCESS("0000", "成功"),

    FAILED("9999", "系统错误"),

    _404("404", "访问错误的资源"),

    _401("401", "身份未验证"),

    _403("403", "拒绝访问");

    private final String code;
    private final String msg;

}

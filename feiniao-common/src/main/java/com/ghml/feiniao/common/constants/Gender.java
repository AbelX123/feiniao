package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:26
 * @description
 */
@Getter
@AllArgsConstructor
public enum Gender {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final int code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        if (code == null) return UNKNOWN.desc;
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender.desc;
            }
        }
        return UNKNOWN.desc;
    }
}

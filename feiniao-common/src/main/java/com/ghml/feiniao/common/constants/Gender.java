package com.ghml.feiniao.common.constants;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:26
 * @description
 */
public enum Gender {
    UNKNOWN(), MALE(), FEMALE();

    Gender() {
    }

    public static String getDescByCode(Integer code) {
        return code == 1 ? "男" : code == 2 ? "女" : "未知";
    }
}

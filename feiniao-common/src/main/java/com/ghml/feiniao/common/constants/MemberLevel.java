package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-30 10:53
 * @description
 */
@Getter
@AllArgsConstructor
public enum MemberLevel {
    ORDINARY(1, "普通会员"),
    SILVER(2, "白银"),
    GOLD(3, "黄金"),
    DIAMOND(4, "钻石");

    private final int code;
    private final String name;

    public static String getNameByCode(Integer code) {
        if (code == null) return ORDINARY.name;
        for (MemberLevel level : values()) {
            if (level.code == code) {
                return level.name;
            }
        }
        return ORDINARY.name;
    }

    public static MemberLevel getByCode(Integer code) {
        if (code == null) return ORDINARY;
        for (MemberLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        return ORDINARY;
    }
}

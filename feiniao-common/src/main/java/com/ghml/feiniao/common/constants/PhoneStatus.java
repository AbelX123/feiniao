package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-08 11:03
 * @description
 */
@Getter
@AllArgsConstructor
public enum PhoneStatus {

    NOT_VERIFIED(0, "未验证"),
    VERIFIED(1, "已验证");

    private final Integer code;
    private final String status;
}
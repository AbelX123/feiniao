package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 10:47
 * @description
 */
@Getter
@AllArgsConstructor
public enum Role {
    BRAND(1, "brand", "产品主"),
    CREATOR(2, "creator", "创作者"),
    OPERATOR(3, "operator", "运营人员");

    private final Integer roleId;
    private final String roleCode;
    private final String roleName;
}

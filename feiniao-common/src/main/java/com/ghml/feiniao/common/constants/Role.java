package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Integer, Role> ROLE_MAP = new HashMap<>();

    static {
        for (Role role : values()) {
            ROLE_MAP.put(role.getRoleId(), role);
        }
    }

    public static Role getByRoleId(Integer roleId) {
        return ROLE_MAP.get(roleId);
    }

    public static String getRoleNameById(Integer roleId) {
        Role role = ROLE_MAP.get(roleId);
        return role != null ? role.getRoleName() : null;
    }
}

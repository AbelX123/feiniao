package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-23 16:30
 * @description
 */
@Data
@TableName(value = "user_roles")
public class UserRolesEntity {

    private String userId;
    private Integer roleId;
}

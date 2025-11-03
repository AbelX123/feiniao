package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 12:43
 * @description
 */
@Data
@TableName("roles")
public class RoleEntity {

    @TableId("role_id")
    private Integer roleId;
    private String roleCode;
    private String roleName;
    private String roleDesc;
    private Date createTime;
}

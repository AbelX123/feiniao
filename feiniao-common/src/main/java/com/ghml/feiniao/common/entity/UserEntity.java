package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 11:33
 * @description
 */
@Data
@TableName("users")
public class UserEntity {

    @TableId("user_id")
    private String userId;
    private String username;
    private String password;
    private Integer roleId;
}

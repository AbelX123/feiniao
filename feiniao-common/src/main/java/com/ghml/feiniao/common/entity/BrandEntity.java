package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 产品主
 */
@Data
@TableName("brand_user")
public class BrandEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 用户编号
    @TableId("user_id")
    private String userId;

    // 用户名称
    private String username;

    // 用户密码
    private String password;

    // 手机号
    private String phone;

    // 头像地址
    private String avatar;

    // 用户等级
    private Integer memberLevel;
}

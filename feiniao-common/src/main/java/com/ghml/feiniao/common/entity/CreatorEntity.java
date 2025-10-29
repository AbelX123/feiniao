package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者
 */
@Data
@TableName("creator_user")
public class CreatorEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("user_id")
    private String userId; // 用户编号

    private String username; // 用户名称
    private String password; // 用户密码
    private BigDecimal videoPrice; // 视频报价
    private String countryCode; // 国家代码
    private Integer gender; // 性别
    private String ageRange; // 年龄范围

    @TableField(exist = false)
    private String countryName; // 关联的country_name
}

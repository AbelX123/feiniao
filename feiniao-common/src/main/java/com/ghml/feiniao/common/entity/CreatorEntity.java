package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者
 */
@Data
@TableName("creator_profiles")
public class CreatorEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("user_id")
    private String userId; // 用户编号
    private String username; // 用户名
    private String avatarUrl; // 创作者头像预签名URL
    private LocalDateTime avatarUrlExpiry; // 头像URL过期时间点
    private String phoneCountryCode; // 手机号国家代码
    private String phoneNumber; // 手机号
    private String phoneFull; // phoneCountryCode + phoneNumber
    private Integer phoneVerified; // 是否验证 0-未验证 1-已验证
    private Date verifiedAt; // 验证时间
    private BigDecimal videoPrice; // 视频报价
    private String countryCode; // 国家代码
    private Integer gender; // 性别
    private String ageRange; // 年龄范围
    private Integer isAvailable; // 是否接单 0-否 1-是

    @TableField(exist = false)
    private String countryName; // 关联的country_name
    @TableField(exist = false)
    private String ageRangeDesc; // 关联的年龄范围
    @TableField(exist = false)
    private Integer isFavorite; // 是否被收藏
}

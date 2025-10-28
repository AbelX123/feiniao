package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatorVo {
    private String userId; // 用户编号
    private String username; // 用户名称
    private String password; // 用户密码
    private BigDecimal videoPrice; // 视频报价
    private String countryCode; // 国家代码
    private Integer gender; // 性别
    private String ageRange; // 年龄范围
    private String countryName; // 关联的country_name
}

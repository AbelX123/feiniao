package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreatorDisplayVo {
    private String userId; // 用户编号
    private String username; // 用户名称
    private BigDecimal videoPrice; // 视频报价
    private String gender; // 性别
    private String ageRangeDesc; // 年龄范围描述
    private Integer isAvailable; //是否接单
    private String countryName; // 关联的国家名
    private String coverUrl; // 展示图
}

package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private String phone_full; // 完整手机号
    private Integer phoneVerified; // 手机号是否经过验证
    private BigDecimal videoPrice; // 视频报价
    private String countryCode; // 国家代码
    private String gender; // 性别
    private String ageRange; // 年龄范围
    private Integer isAvailable; //是否接单

    private String countryName; // 关联的国家名

    private List<String> modelTypes;
    private List<String> platforms;
    private List<String> specialties;
    private List<String> tags;
    private List<CreatorVo.CaseVo> caseVos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaseVo {
        private String caseId;
        private String caseTitle;
        private String coverUrl;
    }
}

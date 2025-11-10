package com.ghml.feiniao.common.dto;

import com.ghml.feiniao.common.annos.ValidPhoneNumberGroup;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-08 23:11
 * @description 创作者dto
 */
@Data
@ValidPhoneNumberGroup
public class CreatorDto {
    // 单一属性
    private String username;
    private String avatar;
    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneFull;
    private Integer phoneVerified;
    private String verifiedCode; // 验证码
    private Date verifiedAt;
    private BigDecimal videoPrice;
    private String countryCode;
    private Integer gender;
    private String ageRange;
    private Integer isAvailable;
    // 关联属性
    private List<String> platformCodes; //拍摄平台
    private List<Integer> modelTypeIds; // 模特类型
    private List<Integer> modelTagIds; // 模特标签
    private List<Integer> specialtyIds; // 擅长品类
}

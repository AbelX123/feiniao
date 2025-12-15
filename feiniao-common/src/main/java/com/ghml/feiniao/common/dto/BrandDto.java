package com.ghml.feiniao.common.dto;

import com.ghml.feiniao.common.annos.ValidPhoneNumberGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@ValidPhoneNumberGroup
public class BrandDto {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是4-20位字母、数字或下划线")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifiedCode;

    private String phoneCountryCode;

    private String phoneFull;
}

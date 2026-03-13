package com.ghml.feiniao.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号验证码登录/注册请求 DTO
 *
 * @author YUHUAI
 */
@Data
public class SignInByPhoneDto {

    @NotBlank(message = "手机号不能为空")
    @JsonAlias({"phone", "phoneFull"})
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @JsonAlias({"captcha", "code", "verifiedCode"})
    private String captcha;

    /**
     * 角色：1=产品主，2=创作者。首次注册时必传，已注册用户可忽略。
     */
    private Integer roleId;
}

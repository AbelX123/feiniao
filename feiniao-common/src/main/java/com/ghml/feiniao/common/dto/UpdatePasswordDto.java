package com.ghml.feiniao.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 修改密码请求 DTO（通过手机号+验证码）
 *
 * @author YUHUAI
 */
@Data
public class UpdatePasswordDto {

    @NotBlank(message = "手机号不能为空")
    @JsonAlias({"phone", "phoneFull"})
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @JsonAlias({"code", "verifiedCode"})
    private String captcha;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度为6-20位")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}

package com.ghml.feiniao.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 10:46
 * @description
 */
@Data
public class UserDto {

    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    private Integer roleId;

    private boolean rememberMe;
}

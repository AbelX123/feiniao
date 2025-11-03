package com.ghml.feiniao.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "角色不能为空")
    private Integer roleId;
}

package com.ghml.feiniao.common.vo;

import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
public class UserVo {
    private String userId;
    private String username;
    private Integer roleId;
    private String accessToken;
    private String refreshToken;
}

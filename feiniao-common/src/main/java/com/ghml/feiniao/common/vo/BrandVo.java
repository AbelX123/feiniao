package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BrandVo {
    private String userId;
    private String username;
    private String phone;
    private AvatarVo avatar;
}

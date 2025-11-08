package com.ghml.feiniao.security.utils;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.security.config.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-30 11:30
 * @description
 */
public class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new ServiceException(Code.USER_NOT_EXIST);
    }

    // 安全获取用户ID（避免异常）
    public static Optional<String> getCurrentUserIdOptional() {
        try {
            return Optional.ofNullable(getCurrentUserId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

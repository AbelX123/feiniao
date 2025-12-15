package com.ghml.feiniao.security.utils;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.security.config.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof MyUserDetails userDetails) {
            return userDetails.getUsername();
        }

        throw new ServiceException(Code.USER_NOT_EXIST);
    }

}

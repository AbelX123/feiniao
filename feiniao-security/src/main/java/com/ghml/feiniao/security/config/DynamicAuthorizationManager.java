package com.ghml.feiniao.security.config;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 20:45
 * @description 自定义动态权限判断器
 */
@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final DynamicPermissionService dynamicPermissionService;

    public DynamicAuthorizationManager(DynamicPermissionService dynamicPermissionService) {
        this.dynamicPermissionService = dynamicPermissionService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext context) {
        if (authentication == null || !authentication.get().isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        Collection<? extends GrantedAuthority> authorities = authentication.get().getAuthorities();
        // 动态获取接口权限
        Set<Object> requiredRoles = dynamicPermissionService.getRequiredPermissions(context);
        boolean hasPermission = authorities.stream()
                .map((Function<GrantedAuthority, String>) GrantedAuthority::getAuthority)
                .anyMatch(requiredRoles::contains);
        return new AuthorizationDecision(hasPermission);
    }
}

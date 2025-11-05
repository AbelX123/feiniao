package com.ghml.feiniao.security.config;

import com.ghml.feiniao.common.service.RedisService;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 20:52
 * @description
 */
@Service
public class DynamicPermissionService {

    private final RedisService redisService;

    public DynamicPermissionService(RedisService redisService) {
        this.redisService = redisService;
    }

    public Set<Object> getRequiredPermissions(RequestAuthorizationContext context) {
        // 通过路径查询角色
        String requestUrl = context.getRequest().getRequestURI();
        return redisService.members(requestUrl);
    }
}

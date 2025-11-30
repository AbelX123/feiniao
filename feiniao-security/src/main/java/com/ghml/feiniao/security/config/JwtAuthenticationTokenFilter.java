package com.ghml.feiniao.security.config;

import com.ghml.feiniao.common.constants.HttpHeaders;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final RedisService redisService;
    private final InMybatisUserDetailsService inMybatisUserDetailsService;

    public JwtAuthenticationTokenFilter(RedisService redisService,
                                        InMybatisUserDetailsService inMybatisUserDetailsService) {
        this.redisService = redisService;
        this.inMybatisUserDetailsService = inMybatisUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 从header中获取token
        String token = request.getHeader(HttpHeaders.ACCESS_TOKEN);

        // 没有token，将请求交给下游过滤器链，此过滤器链不再执行，保持未认证
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 移除 "Bearer " 前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 长度是7
        }
        // 解析token获取userId
        String userId;
        try {
            Claims claims = JwtUtils.parseToken(token);
            userId = claims.getSubject();
            // token中没有subject
            if (StringUtils.isBlank(userId)) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException e) {
            log.warn("token 已过期: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            log.warn("token 解析失败: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        // 从缓存中检查是否有token
        String tokenInCache = (String) redisService.get(RedisPrefix.PREFIX_WEB_TOKEN + userId);
        if (!StringUtils.equals(token, tokenInCache)) {
            log.info("token不一致或缓存token失效, userId={}", userId);
            filterChain.doFilter(request, response);
            return;
        }
        // 封装Authentication对象
        UserDetails details = inMybatisUserDetailsService.loadUserByUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        // 将Authentication存入spring security上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 链式调用
        filterChain.doFilter(request, response);
    }
}

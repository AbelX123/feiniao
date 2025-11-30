package com.ghml.feiniao.security.config;


import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.utils.RespUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description spring-security配置文件
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityConfigProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final MyAuthenticationProvider myAuthenticationProvider;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final SecurityConfigProperties properties;

    // 自定义过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 从配置类中获取放行路径
        String[] publicEndpoints = properties.getPublicEndpoints().toArray(new String[0]);
        http.
                csrf(AbstractHttpConfigurer::disable) //关闭csrf保护
                // 设置会话管理策略，STATELESS——无状态，不创建和使用http session.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置路径管理
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicEndpoints)
                                // 允许通过
                                .permitAll()
                                // 其余必须验证
                                .anyRequest()
                                .authenticated()
//                        .access(dynamicAuthorizationManager)
                );

        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((exceptions) -> exceptions
                // 未认证（没有登录）的处理
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("未认证异常:{}", authException.getMessage());
                    R<Object> r = R.failed(Code._401);
                    RespUtils.response(response, r.toJsonStr());
                })
                // 已认证但无权限访问的处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("无权访问:{}", accessDeniedException.getMessage());
                    R<Object> r = R.failed(Code._403);
                    RespUtils.response(response, r.toJsonStr());
                }));

        return http.build();
    }

    // 将认证操作交给AuthenticationManager管理
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(myAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }
}

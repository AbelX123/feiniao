package com.ghml.feiniao.security.config;


import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.utils.RespUtils;
import lombok.extern.slf4j.Slf4j;
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
public class SecurityConfig {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final MyAuthenticationProvider myAuthenticationProvider;

    // 构造注入
    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, InMybatisUserDetailsService inMybatisUserDetailsService, MyAuthenticationProvider myAuthenticationProvider) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.myAuthenticationProvider = myAuthenticationProvider;
    }

    // 自定义过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) //关闭csrf保护
                // 设置会话管理策略，STATELESS——无状态，不创建和使用http session.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置路径管理
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/api/brands",
                                "/api/brands/login",
                                "/api/brands/refresh-token")
                        // 允许通过
                        .permitAll()
                        // 其余必须验证
                        .anyRequest().authenticated());

        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((exceptions) -> exceptions
                // 未认证（没有登录）的处理
                .authenticationEntryPoint((request, response, authException) -> {
                    R<Object> r = R.failed(Code._401);
                    RespUtils.response(response, r.toJsonStr());
                })
                // 已认证但无权限访问的处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
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

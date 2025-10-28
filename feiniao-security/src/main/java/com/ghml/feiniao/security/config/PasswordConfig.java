package com.ghml.feiniao.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Configuration
public class PasswordConfig {

    // BCrypt算法编辑器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

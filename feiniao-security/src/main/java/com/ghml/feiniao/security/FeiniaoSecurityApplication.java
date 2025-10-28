package com.ghml.feiniao.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ghml.feiniao"})
public class FeiniaoSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoSecurityApplication.class, args);
    }
}

package com.ghml.feiniao.security.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-29 15:47
 * @description
 */
@Getter
@ConfigurationProperties(prefix = "security")
public class SecurityConfigProperties {

    private List<String> publicEndpoints;

    public void setPublicEndpoints(List<String> publicEndpoints) {
        System.out.println("=== Setting public endpoints: " + publicEndpoints);
        this.publicEndpoints = publicEndpoints;
    }

    @PostConstruct
    public void init() {
        System.out.println("=== SecurityConfigProperties initialized ===");
        System.out.println("=== Public endpoints: " + publicEndpoints);
        System.out.println("=== Public endpoints size: " + publicEndpoints.size());
    }
}


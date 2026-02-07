package com.ghml.feiniao.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MCP 调用 feiniao 内部 API 的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "feiniao.mcp.api")
public class FeiniaoApiProperties {

    /**
     * users 模块 API 基础地址，如 http://localhost:8080
     */
    private String usersBaseUrl = "http://localhost:8080";

    /**
     * dicts 模块 API 基础地址，如 http://localhost:8081
     */
    private String dictsBaseUrl = "http://localhost:8081";

    /**
     * 调用 users API 时可选传递的 Authorization 头（如 Bearer xxx）
     */
    private String usersAuthHeader;
}

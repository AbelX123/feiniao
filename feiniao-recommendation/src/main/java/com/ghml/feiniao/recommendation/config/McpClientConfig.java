package com.ghml.feiniao.recommendation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP Client 配置
 * 将 MCP Server 提供的工具注册到 ChatClient 中
 */
@Slf4j
@Configuration
public class McpClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        log.info("初始化 ChatClient，注册 MCP 工具...");
        return builder
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}

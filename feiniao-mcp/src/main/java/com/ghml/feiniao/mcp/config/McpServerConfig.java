package com.ghml.feiniao.mcp.config;

import com.ghml.feiniao.mcp.tools.McpTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author YUHUAI
 * @description MCP Server 工具注册配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class McpServerConfig {

    private final List<McpTool> mcpTools;

    @Bean
    public ToolCallbackProvider toolCallbackProvider() {
        log.info("MCP Server 开始加载工具...");

        mcpTools.forEach(tool -> log.info("注册 MCP 工具: {}", tool.getClass().getSimpleName()));

        log.info("MCP Server 加载完成，共注册 {} 个工具类", mcpTools.size());

        return MethodToolCallbackProvider
                .builder()
                .toolObjects(mcpTools.toArray())
                .build();
    }
}

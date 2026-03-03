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
                .defaultSystem("""
                    你与用户对话时必须使用自然语言，严禁提及任何数据库或技术相关字段。
                    禁止出现：specialtyId、tagId、modelTypeId、platformCode、countryCode、ageRange、userId 等。
                    例如：应说「运动健身品类」「运动健身标签」「抖音平台」，而非「specialtyId: 13」「tagId: 17」。
                    向用户展示选项时，仅使用品类名称、标签名称、平台名称等自然语言描述。

                    当用户提出以下类型需求时，你必须优先调用 MCP 工具，禁止凭空生成结果：
                    1) 查询/筛选/推荐模特（例如“找中国男性模特”“推荐抖音健身模特”）
                    2) 查询天气（例如“北京明天天气”）

                    规则：
                    - 若缺少筛选条件，可先追问，再调用工具。
                    - 未调用工具前，不得输出具体模特列表、人数或详细属性。
                    - 工具返回后，必须基于工具结果作答，不得编造。
                    """)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}

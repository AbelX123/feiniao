package com.ghml.feiniao.recommendation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MCP 工具聊天示例接口
 * 通过 DeepSeek + MCP 工具实现智能对话
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpChatController {

    private final ChatClient chatClient;

    /**
     * 基于 MCP 工具的智能对话
     * 示例: GET /api/mcp/chat?message=北京今天天气怎么样
     * DeepSeek 会自动识别需要调用天气工具，通过 MCP 获取结果后返回
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        log.info("收到 MCP 聊天请求: {}", message);
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        log.info("MCP 聊天响应: {}", response);
        return response;
    }
}

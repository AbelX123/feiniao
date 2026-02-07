package com.ghml.feiniao.recommendation.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import com.ghml.feiniao.recommendation.config.ConversationStore;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 聊天处理器，支持多轮对话（会话历史 + MCP 工具）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatClient chatClient;
    private final ConversationStore conversationStore;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 连接建立: {}", session.getId());
        JSONObject welcomeMsg = new JSONObject();
        welcomeMsg.put("type", "system");
        welcomeMsg.put("content", "连接成功，可以开始对话了！支持多轮澄清，例如问天气时会追问日期。");
        session.sendMessage(new TextMessage(JSON.toJSONString(welcomeMsg)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("收到消息: {}", payload);

        try {
            JSONObject request = JSON.parseObject(payload);
            String userMessage = request.getString("message");

            if (userMessage == null || userMessage.trim().isEmpty()) {
                JSONObject errorMsg = new JSONObject();
                errorMsg.put("type", "error");
                errorMsg.put("content", "消息内容不能为空");
                session.sendMessage(new TextMessage(JSON.toJSONString(errorMsg)));
                return;
            }

            // 发送开始标记
            JSONObject startMsg = new JSONObject();
            startMsg.put("type", "start");
            startMsg.put("content", "");
            sendMessageSafely(session, startMsg);

            // 构建带历史的多轮 prompt
            List<Message> msgs = new ArrayList<>(conversationStore.getHistory(session.getId()));
            msgs.add(new UserMessage(userMessage));
            Prompt prompt = new Prompt(msgs);

            // 用于累积 assistant 回复
            StringBuilder accumulated = new StringBuilder();

            // 使用 Spring AI ChatClient 流式返回（DeepSeek + MCP 工具 + 历史）
            chatClient.prompt(prompt)
                    .stream()
                    .content()
                    .subscribe(
                            chunk -> {
                                accumulated.append(chunk);
                                try {
                                    JSONObject chunkMsg = new JSONObject();
                                    chunkMsg.put("type", "chunk");
                                    chunkMsg.put("content", chunk);
                                    sendMessageSafely(session, chunkMsg);
                                } catch (Exception e) {
                                    log.error("发送流式数据块失败: {}", e.getMessage(), e);
                                }
                            },
                            error -> {
                                log.error("流式处理异常: {}", error.getMessage(), error);
                                try {
                                    JSONObject errorMsg = new JSONObject();
                                    errorMsg.put("type", "error");
                                    errorMsg.put("content", "处理消息时发生错误: " + error.getMessage());
                                    sendMessageSafely(session, errorMsg);
                                } catch (Exception e) {
                                    log.error("发送错误消息失败: {}", e.getMessage(), e);
                                }
                            },
                            () -> {
                                try {
                                    conversationStore.append(session.getId(), userMessage, accumulated.toString());
                                    JSONObject endMsg = new JSONObject();
                                    endMsg.put("type", "end");
                                    endMsg.put("content", "");
                                    sendMessageSafely(session, endMsg);
                                } catch (Exception e) {
                                    log.error("发送结束消息失败: {}", e.getMessage(), e);
                                }
                            }
                    );

        } catch (Exception e) {
            log.error("解析消息失败: {}", e.getMessage(), e);
            JSONObject errorMsg = new JSONObject();
            errorMsg.put("type", "error");
            errorMsg.put("content", "消息格式错误，请发送 JSON 格式: {\"message\": \"你的问题\"}");
            sendMessageSafely(session, errorMsg);
        }
    }

    /**
     * 安全发送消息，捕获异常
     */
    private void sendMessageSafely(WebSocketSession session, JSONObject message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(JSON.toJSONString(message)));
            }
        } catch (IOException e) {
            log.error("发送 WebSocket 消息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输错误: {}", exception.getMessage(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 连接关闭: {}, 状态: {}", session.getId(), status);
        conversationStore.remove(session.getId());
    }
}

package com.ghml.feiniao.recommendation.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ghml.feiniao.recommendation.client.DeepSeekClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * webSocket 聊天处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final DeepSeekClient deepSeekClient;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 连接建立: {}", session.getId());
        JSONObject welcomeMsg = new JSONObject();
        welcomeMsg.put("type", "system");
        welcomeMsg.put("content", "连接成功，可以开始对话了！");
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

            // 流式调用 DeepSeek API
            deepSeekClient.sendMessageStream(
                    userMessage,
                    // onChunk: 接收到每个数据块时发送
                    chunk -> {
                        try {
                            JSONObject chunkMsg = new JSONObject();
                            chunkMsg.put("type", "chunk");
                            chunkMsg.put("content", chunk);
                            sendMessageSafely(session, chunkMsg);
                        } catch (Exception e) {
                            log.error("发送流式数据块失败: {}", e.getMessage(), e);
                        }
                    },
                    // onError: 错误处理
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
                    // onComplete: 完成标记
                    () -> {
                        try {
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
    }
}

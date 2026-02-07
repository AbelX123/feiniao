package com.ghml.feiniao.recommendation.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按 WebSocket sessionId 保存会话历史，用于多轮对话
 */
@Component
public class ConversationStore {

    private static final int MAX_MESSAGES = 20;

    private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        List<Message> list = sessionHistory.get(sessionId);
        return list == null ? List.of() : new ArrayList<>(list);
    }

    public void append(String sessionId, String userText, String assistantText) {
        sessionHistory.compute(sessionId, (k, list) -> {
            List<Message> messages = list != null ? new ArrayList<>(list) : new ArrayList<>();
            messages.add(new UserMessage(userText));
            messages.add(new AssistantMessage(assistantText));
            if (messages.size() > MAX_MESSAGES) {
                messages = messages.subList(messages.size() - MAX_MESSAGES, messages.size());
            }
            return messages;
        });
    }

    public void remove(String sessionId) {
        sessionHistory.remove(sessionId);
    }
}

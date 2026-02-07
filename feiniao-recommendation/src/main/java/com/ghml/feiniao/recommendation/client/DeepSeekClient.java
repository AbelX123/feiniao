package com.ghml.feiniao.recommendation.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ghml.feiniao.recommendation.config.DeepSeekProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * DeepSeek API 客户端（流式）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {

    private final DeepSeekProperties deepSeekProperties;
    private final WebClient webClient;

    /**
     * 流式发送消息到 DeepSeek API
     *
     * @param message    用户消息
     * @param onChunk    接收到每个数据块时的回调
     * @param onError    错误回调
     * @param onComplete 完成回调
     */
    public void sendMessageStream(String message,
                                  Consumer<String> onChunk,
                                  Consumer<Throwable> onError,
                                  Runnable onComplete) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepSeekProperties.getModel());
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", message)
            ));
            requestBody.put("stream", true);

            webClient.post()
                    .uri(deepSeekProperties.getApiUrl())
                    .header("Authorization", "Bearer " + deepSeekProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(JSON.toJSONString(requestBody))
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .timeout(Duration.ofMillis(deepSeekProperties.getTimeout()))
                    .subscribe(
                            dataBuffer -> {
                                try {
                                    // 将 DataBuffer 转换为字符串
                                    String data = dataBuffer.toString(StandardCharsets.UTF_8);
                                    // 处理 SSE 格式的数据
                                    if (!data.trim().isEmpty()) {
                                        String[] lines = data.split("\n");
                                        for (String line : lines) {
                                            line = line.trim();
                                            if (line.startsWith("data: ")) {
                                                String jsonStr = line.substring(6).trim();
                                                // 忽略 [DONE] 标记，让 Flux 的完成回调统一处理
                                                if ("[DONE]".equals(jsonStr)) {
                                                    return;
                                                }
                                                if (!jsonStr.isEmpty()) {
                                                    try {
                                                        JSONObject jsonResponse = JSON.parseObject(jsonStr);
                                                        if (jsonResponse.containsKey("choices")) {
                                                            JSONObject choice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                                                            JSONObject delta = choice.getJSONObject("delta");
                                                            if (delta != null && delta.containsKey("content")) {
                                                                String content = delta.getString("content");
                                                                if (content != null && !content.isEmpty()) {
                                                                    onChunk.accept(content);
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        log.warn("解析流式数据失败: {}", jsonStr, e);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } finally {
                                    // 释放 DataBuffer
                                    if (dataBuffer != null) {
                                        DataBufferUtils.release(dataBuffer);
                                    }
                                }
                            },
                            error -> {
                                log.error("流式调用 DeepSeek API 异常: {}", error.getMessage(), error);
                                if (onError != null) {
                                    onError.accept(error);
                                }
                            },
                            () -> {
                                // 只在 Flux 流完成时调用一次
                                if (onComplete != null) {
                                    onComplete.run();
                                }
                            }
                    );
        } catch (Exception e) {
            log.error("调用 DeepSeek API 异常: {}", e.getMessage(), e);
            if (onError != null) {
                onError.accept(e);
            }
        }
    }
}

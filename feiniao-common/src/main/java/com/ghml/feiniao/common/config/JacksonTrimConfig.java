package com.ghml.feiniao.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 15:33
 * @description Jackson双向全局Body去空格
 */
@Configuration
public class JacksonTrimConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. 添加去空格模块（你原有的）
        SimpleModule trimModule = new SimpleModule();
        trimModule.addDeserializer(String.class, new JsonDeserializer<>() {
            @Override
            public String deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext) throws IOException {
                String value = jsonParser.getValueAsString();
                return value == null ? null : value.trim();
            }
        });
        trimModule.addSerializer(String.class, new JsonSerializer<>() {
            @Override
            public void serialize(String s, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(s == null ? null : s.trim());
            }
        });

        // 2. 添加 JavaTimeModule（支持 LocalDateTime）
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));

        // 3. 注册两个模块
        mapper.registerModule(trimModule);
        mapper.registerModule(javaTimeModule);

        // 4. 禁用时间戳格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 5. 设置其他配置
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }
}

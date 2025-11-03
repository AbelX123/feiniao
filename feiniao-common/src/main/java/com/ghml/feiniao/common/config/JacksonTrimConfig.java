package com.ghml.feiniao.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 15:33
 * @description Jackson双向全局Body去空格
 */
@Configuration
public class JacksonTrimConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new JsonDeserializer<>() {
            @Override
            public String deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext) throws IOException {
                String value = jsonParser.getValueAsString();
                return value == null ? null : value.trim();
            }
        });
        module.addSerializer(String.class, new JsonSerializer<>() {
            @Override
            public void serialize(String s, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(s == null ? null : s.trim());
            }
        });
        mapper.registerModule(module);
        return mapper;
    }
}

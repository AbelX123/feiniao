package com.ghml.feiniao.users.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-31 15:00
 * @description
 */
@Data
@Configuration
public class MinIOConfig {

    private final MinIOProps minIOProps;

    public MinIOConfig(MinIOProps minIOProps) {
        this.minIOProps = minIOProps;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minIOProps.getEndpoint())
                .credentials(minIOProps.getAccessKey(),
                        minIOProps.getSecretKey())
                .build();
    }
}

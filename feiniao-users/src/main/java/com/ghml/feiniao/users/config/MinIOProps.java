package com.ghml.feiniao.users.config;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-31 15:02
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinIOProps {
    // 连接地址
    private String endpoint;
    // 用户名
    private String accessKey;
    // 密码
    private String secretKey;
    // cdn前缀
    private String cdnPrefix;
    // 头像外链时间
    private Integer avatarExpiry;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

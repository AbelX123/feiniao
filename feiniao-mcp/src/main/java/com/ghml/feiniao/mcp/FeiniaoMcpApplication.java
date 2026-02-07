package com.ghml.feiniao.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author YUHUAI
 * @description MCP Server 启动类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FeiniaoMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoMcpApplication.class, args);
    }

}

package com.ghml.feiniao.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author YUHUAI
 * @description MCP Server 启动类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.ghml.feiniao"})
public class FeiniaoMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoMcpApplication.class, args);
    }

}

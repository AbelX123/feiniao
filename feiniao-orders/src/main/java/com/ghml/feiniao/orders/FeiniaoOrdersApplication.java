package com.ghml.feiniao.orders;

import org.springframework.boot.SpringApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@SpringBootApplication
@MapperScan("com.ghml.feiniao.common.mapper")
@ComponentScan(basePackages = {"com.ghml.feiniao"})
public class FeiniaoOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoOrdersApplication.class, args);
    }

}

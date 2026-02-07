package com.ghml.feiniao.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.ghml.feiniao"})
public class FeiniaoRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoRecommendationApplication.class, args);
    }

}

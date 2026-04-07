package com.ghml.feiniao.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class FeiniaoPaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeiniaoPaymentsApplication.class, args);
    }

}

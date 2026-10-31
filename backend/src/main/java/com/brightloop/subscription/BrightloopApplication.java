package com.brightloop.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.brightloop.subscription.config",
    "com.brightloop.subscription.controller",
    "com.brightloop.subscription.service",
    "com.brightloop.subscription.repository"
})
@EntityScan(basePackages = {"com.brightloop.subscription.model"})
@EnableJpaRepositories(basePackages = {"com.brightloop.subscription.repository"})
@EnableScheduling
public class BrightloopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrightloopApplication.class, args);
    }
}

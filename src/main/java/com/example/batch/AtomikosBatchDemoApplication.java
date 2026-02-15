package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration.class})
public class AtomikosBatchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtomikosBatchDemoApplication.class, args);
    }

}

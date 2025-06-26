package com.leetwise;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
@EnableScheduling
public class LeetwiseApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LeetwiseApplication.class).run(args);
    }
}

package com.leetwise;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.builder.SpringApplicationBuilder;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class LeetwiseApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        new SpringApplicationBuilder(LeetwiseApplication.class)
            .properties(
                "spring.datasource.url=" + dotenv.get("DB_URL"),
                "spring.datasource.username=" + dotenv.get("DB_USERNAME"),
                "spring.datasource.password=" + dotenv.get("DB_PASSWORD"),
                "judge0.api.url=" + dotenv.get("JUDGE0_API_URL"),
                "judge0.api.key=" + dotenv.get("JUDGE0_API_KEY")
            )
            .run(args);
    }
}

package com.akshat.college_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class CollegeProjectApplication {

    public static void main(String[] args) {
        io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().systemProperties().load();
        SpringApplication.run(CollegeProjectApplication.class, args);
    }

    @Bean
    public org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder() {
        return org.springframework.web.reactive.function.client.WebClient.builder();
    }

    @Bean
    public CommandLineRunner schemaFix(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE team_member_status ADD COLUMN rejected_member_array jsonb DEFAULT '[]' NOT NULL");
            } catch (Exception e) {
                // Column likely already exists or permissions prevented it, safe to ignore
            }
        };
    }
}

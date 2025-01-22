package com.sk.skala.quizapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class })
public class SkalaQuizApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkalaQuizApiApplication.class, args);
	}

}

package com.sk.skala.quizapi.config;

import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProperties {
	private String name;
	private Log log;
	private Health health;

	@Data
	public static class Log {
		private List<String> customHeaders;
		private Set<String> hiddens;
	}

	@Data
	public static class Health {
		String url;
		long timeout;
	}
}

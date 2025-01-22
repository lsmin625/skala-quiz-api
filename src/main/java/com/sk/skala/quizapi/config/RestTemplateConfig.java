package com.sk.skala.quizapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Constant.REST_CONNECTION_TIMEOUT);
		factory.setReadTimeout(Constant.REST_READ_TIMEOUT);

		return new RestTemplate(factory);
	}
}
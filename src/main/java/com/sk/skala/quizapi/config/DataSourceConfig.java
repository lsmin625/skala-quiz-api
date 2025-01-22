package com.sk.skala.quizapi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Primary
	@Bean(name = "batchDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.batch")
	HikariDataSource batchDataSource() {
		return new HikariDataSource();
	}

	@Bean(name = "ossDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.oss")
	HikariDataSource ossDataSource() {
		return new HikariDataSource();
	}

	@Primary
	@Bean(name = "batchTransactionManager")
	PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource") HikariDataSource batchDataSource) {
		return new DataSourceTransactionManager(batchDataSource);
	}
}

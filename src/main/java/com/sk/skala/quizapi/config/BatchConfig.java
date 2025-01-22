package com.sk.skala.quizapi.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	@Qualifier("batchDataSource")
	private final HikariDataSource batchDataSource;

	@Qualifier("batchTransactionManager")
	private final PlatformTransactionManager batchTransactionManager;

	@Bean
	JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(batchDataSource);
		factory.setTransactionManager(batchTransactionManager);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	JobExplorer jobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
		factory.setDataSource(batchDataSource);
		factory.setTransactionManager(batchTransactionManager);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}

	@Bean
	JobOperator jobOperator(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository,
			JobRegistry jobRegistry) throws Exception {
		SimpleJobOperator operator = new SimpleJobOperator();
		operator.setJobLauncher(jobLauncher);
		operator.setJobExplorer(jobExplorer);
		operator.setJobRepository(jobRepository);
		operator.setJobRegistry(jobRegistry);
		return operator;
	}
}

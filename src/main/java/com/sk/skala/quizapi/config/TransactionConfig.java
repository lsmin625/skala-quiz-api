package com.sk.skala.quizapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.sk.msa.sonpocapi.repository", entityManagerFactoryRef = "ossEntityManagerFactory", transactionManagerRef = "ossTransactionManager")
public class TransactionConfig {

	@Qualifier("ossDataSource")
	private final HikariDataSource ossDataSource;

	@Bean(name = "ossEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean ossEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("ossDataSource") HikariDataSource ossDataSource) {

		Map<String, String> properties = new HashMap<>();
		properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY,
				"org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
		properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY,
				"org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

		return builder.dataSource(ossDataSource).packages("com.sk.msa.sonpocapi.data").persistenceUnit("ossJpaUnit")
				.properties(properties).build();
	}

	@Bean(name = "ossTransactionManager")
	PlatformTransactionManager ossTransactionManager(
			@Qualifier("ossEntityManagerFactory") EntityManagerFactory ossEntityManagerFactory) {
		return new JpaTransactionManager(ossEntityManagerFactory);
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	public static class AuditorAwareImpl implements AuditorAware<String> {
		@Override
		public Optional<String> getCurrentAuditor() {
			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = sra.getRequest();
			String userId = request.getHeader(Constant.X_BFF_USER);
			return Optional.ofNullable(userId);
		}
	}
}

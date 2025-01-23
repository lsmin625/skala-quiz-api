package com.sk.skala.quizapi.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.quizapi.data.common.AccountInfo;
import com.sk.skala.quizapi.tools.JsonTool;
import com.sk.skala.quizapi.tools.JwtTool;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class TransactionConfig {
	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
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
			if (userId != null) {
				return Optional.ofNullable(userId);
			} else {
				AccountInfo account = getAccountInfo(request.getCookies());
				return Optional.ofNullable(account.getUserId());
			}
		}
	}

	private static AccountInfo getAccountInfo(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Constant.JWT_ACCESS_COOKIE.equals(cookie.getName())) {
					String payload = JwtTool.getValidPayload(cookie.getValue(), Constant.JWT_SECRET_OAS);
					return JsonTool.toObject(payload, AccountInfo.class);
				}
			}
		}
		return new AccountInfo();
	}
}

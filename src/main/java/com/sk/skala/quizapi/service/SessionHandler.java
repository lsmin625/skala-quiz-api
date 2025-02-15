package com.sk.skala.quizapi.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.quizapi.config.Constant;
import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.AccountInfo;
import com.sk.skala.quizapi.data.table.Instructor;
import com.sk.skala.quizapi.data.table.Subject;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.tools.JsonTool;
import com.sk.skala.quizapi.tools.JwtTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SessionHandler {

	public AccountInfo getAccountInfo() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Constant.JWT_ACCESS_COOKIE.equals(cookie.getName())) {
					String payload = JwtTool.getValidPayload(cookie.getValue(), Constant.JWT_SECRET_BFF);
					return JsonTool.toObject(payload, AccountInfo.class);
				}
			}
		} else {
			return getAccountInfoByApiKey();
		}
		throw new ResponseException(Error.SESSION_NOT_FOUND);
	}

	public String getAccountId() {
		AccountInfo accountInfo = getAccountInfo();
		return accountInfo != null ? accountInfo.getAccountId() : null;
	}

	public boolean isAdmin() {
		AccountInfo account = getAccountInfoByApiKey();
		return AccountInfo.ROLE_ADMIN == account.getAccountRole();
	}

	private AccountInfo getAccountInfoByApiKey() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = sra.getRequest();
		String apiKey = request.getHeader(Constant.API_KEY);
		if (Constant.API_VALUE.equals(apiKey)) {
			AccountInfo account = new AccountInfo();
			account.setAccountId("root");
			account.setAccountName("root");
			account.setAccountRole(AccountInfo.ROLE_ADMIN);
			return account;
		} else {
			throw new ResponseException(Error.SESSION_NOT_FOUND);
		}
	}

	public AccountInfo storeAccessToken(Instructor instructor, List<Subject> subjects) {
		AccountInfo account = new AccountInfo(instructor.getId(), instructor.getInstructorEmail(),
				instructor.getInstructorName());

		return storeAccessToken(account, subjects);
	}

	public AccountInfo storeAccessToken(AccountInfo account, List<Subject> subjects) {
		List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
		account.setSubjectIds(subjectIds);

		String token = JwtTool.generateToken(account.getAccountId(), account, Constant.JWT_SECRET_BFF);
		Cookie cookie = new Cookie(Constant.JWT_ACCESS_COOKIE, token);
		cookie.setMaxAge(Constant.JWT_ACCESS_TTL);
		cookie.setPath("/");
		cookie.setSecure(false);

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.getResponse().addCookie(cookie);

		return account;
	}

}
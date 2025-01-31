package com.sk.skala.quizapi.data.common;

import java.util.List;

import lombok.Data;

@Data
public class AccountInfo {
	public static final Integer ROLE_READER = 1;
	public static final Integer ROLE_WRITER = 2;
	public static final Integer ROLE_ADMIN = 3;

	String accountId;
	String accountName;
	Integer accountRole;
	List<Long> subjectIds;

	public AccountInfo() {
	}

	public AccountInfo(String id, String name) {
		this(id, name, ROLE_READER);
	}

	public AccountInfo(String id, String name, Integer role) {
		this.accountId = id;
		this.accountName = name;
		this.accountRole = role;
	}
}

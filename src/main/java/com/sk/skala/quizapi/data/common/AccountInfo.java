package com.sk.skala.quizapi.data.common;

import java.util.List;

import lombok.Data;

@Data
public class AccountInfo {
	public static final Integer ROLE_READER = 1;
	public static final Integer ROLE_WRITER = 2;
	public static final Integer ROLE_ADMIN = 3;

	Long instructorId;
	String accountId;
	String accountName;
	Integer accountRole;
	List<Long> subjectIds;

	public AccountInfo() {
	}

	public AccountInfo(String id, String name) {
		this.instructorId = null;
		this.accountId = id;
		this.accountName = name;
		this.accountRole = ROLE_READER;
	}

	public AccountInfo(Long instructorId, String id, String name) {
		this.instructorId = instructorId;
		this.accountId = id;
		this.accountName = name;
		this.accountRole = ROLE_WRITER;
	}
}

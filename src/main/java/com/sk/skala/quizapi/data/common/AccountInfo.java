package com.sk.skala.quizapi.data.common;

import java.util.List;

import lombok.Data;

@Data
public class AccountInfo {
	Long organizationId;
	String organizationName;
	String userId;
	String userPassword;
	String userName;
	String userRole;
	List<Long> applicationIds;
}

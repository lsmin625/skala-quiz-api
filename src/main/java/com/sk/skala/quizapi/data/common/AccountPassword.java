package com.sk.skala.quizapi.data.common;

import lombok.Data;

@Data
public class AccountPassword {

	String accountId;
	String oldPassword;
	String newPassword;
}

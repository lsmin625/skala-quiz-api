package com.sk.skala.quizapi.config;

public enum Error {

	//@formatter:off
	SYSTEM_ERROR(9000, "SYSTEM_ERROR"),
	
	// codes should be shared with UI
	NOT_AUTHENTICATED(9001, "NOT_AUTHENTICATED"),
	SESSION_NOT_FOUND(9004, "SESSION_NOT_FOUND"),

	NOT_AUTHORIZED(9002, "NOT_AUTHORIZED"),

	DATA_DUPLICATED(9006, "DATA_DUPLICATED"),
	PARAMETER_MISSED(9007, "PARAMETER_MISSED"),
	DATA_NOT_FOUND(9008, "DATA_NOT_FOUND"),

	INVALID_EXCEL_FORMAT(9011, "INVALID_EXCEL_FORMAT"),

	INVALID_ID_OR_PASSWORD(9012, "INVALID_ID_OR_PASSWORD"),

	INVALID_ORGANIZATION(9101, "INVALID_ORGANIZATION"),
	MULTIPLE_ORGANIZATION_USER(9103, "MULTIPLE_ORGANIZATION_USER"),
	UNKNOWN_ORGANIZATION_USER(9104, "INVALID_ORGANIZATION_USER"),

	UNDEFINED_ERROR(9999, "UNDEFINED_ERROR");
	//@formatter:on

	private final int code;
	private final String message;

	Error(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
package com.sk.skala.quizapi.exception;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.tools.StringTool;

public class ParameterException extends RuntimeException {
	private static final long serialVersionUID = -1485573803677705666L;
	private final int code;

	public ParameterException(String... parameters) {
		super(Error.PARAMETER_MISSED.getMessage() + ": " + StringTool.join(parameters));
		this.code = Error.PARAMETER_MISSED.getCode();
	}

	public ParameterException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}
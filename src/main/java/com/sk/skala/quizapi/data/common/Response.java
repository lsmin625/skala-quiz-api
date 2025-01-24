package com.sk.skala.quizapi.data.common;

import java.io.Serializable;

import com.sk.skala.quizapi.config.Error;

import lombok.Data;

@Data
public class Response implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int SUCCESS = 0;
	public static final int FAIL = 1;

	private int result;
	private int code;
	private String message;
	private Object body;

	public void setError(Error error) {
		this.result = FAIL;
		this.code = error.getCode();
		this.message = error.getMessage();
	}

	public void setError(int code, String message) {
		this.result = FAIL;
		this.code = code;
		this.message = message;
	}
}

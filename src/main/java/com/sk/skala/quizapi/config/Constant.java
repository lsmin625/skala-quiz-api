package com.sk.skala.quizapi.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * All constants for applications
 *
 * @author Lee, Sangmin.
 * @since 2023.08
 *
 */
public class Constant {
	public static final int EXCEL_MAX_STEPS = 100;

	// to log request or response body
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";
	public static final Set<String> TEXT_TYPES = new HashSet<>(
			Arrays.asList("application/json", "text/plain", "text/xml"));
	public static final String HIDDEN = "*****";

	public static final String PROFILE_PRODUCT = "prd";

	// headers for backend applications after session checking
	public static final String X_BFF_USER = "X-Bff-User";

	public static final String JWT_ACCESS_COOKIE = "oas-access";
	public static final int JWT_ACCESS_TTL = 24 * 60 * 60; // 24 hours

	public static final String JWT_SECRET_BFF = "cookieBaker-cookieMonster-cookieLover-cookieJar-cookieCrumbs123!";
	public static final String JWT_SECRET_OAS = "breadMaker-breadWinner-breadCrumbs-breadBasket-breadSlice2024!";
	public static final String JWT_ISSUER = "oas-doc";
	public static final String JWT_SUBJECT = "oas-doc-token";
	public static final int JWT_TTL_MILLIS = JWT_ACCESS_TTL * 1000;

	public static final String API_KEY = "X-Api-Key";
	public static final String API_VALUE = "bytebiters";
}

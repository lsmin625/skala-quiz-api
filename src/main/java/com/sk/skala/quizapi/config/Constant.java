package com.sk.skala.quizapi.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant {
	// maximum excel data row count
	public static final int EXCEL_MAX_ROWS = 10000;
	public static final int EXCEL_PAGE_ROWS = 100;

	// settings for password
	public static final String PASSWROD_RANDOM_KEY = "cbzCoo3j2gejXbJlTlBccg==";
	public static final int PASSWORD_MAX_TRY = 3;
	public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_+=<>?])[A-Za-z\\d!@#$%^&*()\\-_+=<>?]{8,}$";
	public static final long PASSWORD_LONGTIME = 180 * 24 * 60 * 60 * 1000; // 180 days ago

	// spring active profile for production
	public static final String PROFILE_PRODUCT = "prd";

	// default useGroupId
	public static final String DEFAULT_USER_GROUP_ID = "none";

	// to permit API access by client ID & Key(Token)
	public static final String API_CLIENT_ID = "Api-Client-Id";
	public static final String API_CLIENT_KEY = "Api-Client-Key";

	// to get client IP Address
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";

	// to log request or response body
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";
	public static final Set<String> TEXT_TYPES = new HashSet<>(
			Arrays.asList("application/json", "text/plain", "text/xml"));
	public static final String HIDDEN = "*****";

	// headers for backend applications after session checking
	public static final String X_BFF_USER = "X-Bff-User";
	public static final String X_BFF_TID = "X-Bff-Tid";
	public static final String X_BFF_SESSION = "X-Bff-Session";

	// label for cookie session
	public static final String BFF_SESSION_COOKIE = "Bff-Session";
	public static final int BFF_COOKIE_TTL = 24 * 60 * 60; // 24 hours

	// prefix for redis session
	public static final String BFF_SESSION_REDIS = "Bff-Session:";
	public static final String BFF_LOGIN_REDIS = "Bff-Login:";
	public static final String BFF_PASSWORD_REDIS = "Bff-Password:";
	public static final String BFF_CODE_REDIS = "Bff-Code:";

	public static final String MASKING_AUTHCODE_REDIS = "Masking-Authcode:";
	public static final String MASKING_UNLOCK_REDIS = "Masking-Unlock:";

	// prefix for saga transaction
	public static final String SAGA_TRANSACTION_REDIS = "Saga-Transaction:";
	public static final String SAGA_BACKEND_REDIS = "Saga-Backend:";

	// session expiration after last update
	public static final Duration DURATION_SESSION = Duration.ofSeconds(60 * 60); // 60 minutes

	// re-login term after MAX_PASSWORD_TRY error
	public static final Duration DURATION_PASSWORD = Duration.ofSeconds(30 * 60); // 30 minutes

	public static final Duration DURATION_AUTHCODE = Duration.ofSeconds(3 * 60); // 3 minutes
	public static final Duration DURATION_MASKING = Duration.ofSeconds(10 * 60); // 10 minutes

	public static final String JWT_SECRET_BFF = "cookieBaker-cookieMonster-cookieLover-cookieJar-cookieCrumbs123!";
	public static final String JWT_ISSUER = "bff-base";
	public static final String JWT_SUBJECT = "bff-base-token";
	public static final int JWT_TTL_MILLIS = 60 * 60 * 1000;

	// setting for restTemplate
	public static final int REST_CONNECTION_TIMEOUT = 5000;
	public static final int REST_READ_TIMEOUT = 5000;

	public static final String JOB_EXIT_STATUS = "job-exit-status";
	public static final String JOB_EXIT_MESSAGE = "job-exit-message";
	public static final String JOB_ITEMS_READ = "job-items-read";
	public static final String JOB_ITEMS_WRITE = "job-items-write";

	public static final String EXIT_STATUS_ERROR = "ERROR";

}

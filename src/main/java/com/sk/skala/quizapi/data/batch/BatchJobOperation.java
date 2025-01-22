package com.sk.skala.quizapi.data.batch;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BatchJobOperation {
	public static final String STYLE_NORMAL = "normal";
	public static final String STYLE_PARALLEL = "parallel";
	public static final String STYLE_SHELL = "shell";
	public static final String STYLE_ONDEMAND = "ondemand";

	public static final String TYPE_CRON = "cron";
	public static final String TYPE_FIXED_DELAY = "fixedDelay";
	public static final String TYPE_FIXED_RATE = "fixedRate";
	public static final String TYPE_ONDEMAND = "ondemand";

	public static final String START = "start";
	public static final String STOP = "stop";
	public static final String STATUS = "status";
	public static final String ONDEMAND = "ondemand";

	Long jobId;
	String jobGroup;
	String jobName;
	String jobUrl;
	String jobDescription;

	String jobStyle; // Style of the job: normal, parallel, shell, etc.
	String jobType; // Type of the job: cron, fixedDelay, fixedRate, etc.
	String jobCron; // Used for cron expressions
	Long jobInterval; // Used for fixedDelay and fixedRate in seconds
	Long jobInitialDelay; // Used for initial delay for fixedDelay and fixedRate in seconds

	Map<String, Object> jobParameters;

	public static boolean isValidOperation(String operation) {
		if (START.equals(operation) || STOP.equals(operation) || STATUS.equals(operation)
				|| ONDEMAND.equals(operation)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidStyle() {
		if (STYLE_NORMAL.equals(jobStyle) || STYLE_PARALLEL.equals(jobStyle) || STYLE_SHELL.equals(jobStyle)
				|| STYLE_ONDEMAND.equals(jobStyle)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValidType() {
		if (TYPE_CRON.equals(jobType) || TYPE_FIXED_DELAY.equals(jobType) || TYPE_FIXED_RATE.equals(jobType)
				|| TYPE_ONDEMAND.equals(jobType)) {
			return true;
		} else {
			return false;
		}
	}
}
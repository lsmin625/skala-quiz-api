package com.sk.skala.quizapi.data.batch;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BatchJobStatus {
	public static final String ABANDONED = "ABANDONED"; // Did not stop properly and can not be restarted.
	public static final String COMPLETED = "COMPLETED"; // The batch job has successfully completed its execution.
	public static final String FAILED = "FAILED"; // Status that has failed during its execution.
	public static final String STARTED = "STARTED"; // Status that is running.
	public static final String STARTING = "STARTING"; // Status of a batch job prior to its execution.
	public static final String STOPPED = "STOPPED"; // Status that has been stopped by request.
	public static final String STOPPING = "STOPPING"; // Status of waiting for a step to complete stopping
	public static final String UNKNOWN = "UNKNOWN"; // Status that is in an uncertain state.

	public static final String EXIT_COMPLETED = "COMPLETED"; // Representing finished
	public static final String EXIT_EXECUTING = "EXECUTING"; // Representing state where processing is taking place
	public static final String EXIT_FAILED = "FAILED"; // Representing finished processing
	public static final String EXIT_NOOP = "NOOP"; // Representing a job that did no processing (already complete).
	public static final String EXIT_STOPPED = "STOPPED"; // Representing finished processing with interrupted status.
	public static final String EXIT_UNKNOWN = "UNKNOWN"; // Representing unknown state - assumed to not be continuable.

	Long jobId;
	String jobGroup;
	String jobName;
	String jobUrl;
	String jobDescription;
	String jobStyle;

	Long jobInstanceId;
	Long jobExecutionId;

	String jobStartTime;
	String jobEndTime;

	String jobStatus;
	String jobExitStatus;
	String jobExitMessage;

	public BatchJobStatus() {
	}

	public BatchJobStatus(BatchJobOperation info) {
		this.jobId = info.getJobId();
		this.jobGroup = info.getJobGroup();
		this.jobName = info.getJobName();
		this.jobUrl = info.getJobUrl();
		this.jobDescription = info.getJobDescription();
		this.jobStyle = info.getJobStyle();
	}
}
package com.sk.skala.quizapi.data.batch;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BatchJobShell {
	String jobGroup;
	String jobName;
	String jobDescription;

	String shell;
	String callback;

	Date jobStartTime;
	Date jobEndTime;

	String jobExitStatus;
	String jobExitMessage;
	Long jobItemsRead;
	Long jobItemsWrite;

	public BatchJobShell() {
	}

	public BatchJobShell(BatchJobOperation batchJobOperation) {
		this.jobGroup = batchJobOperation.getJobGroup();
		this.jobName = batchJobOperation.getJobName();
		this.jobDescription = batchJobOperation.getJobDescription();
		this.shell = (String) batchJobOperation.getJobParameters().get("shell");
		this.callback = (String) batchJobOperation.getJobParameters().get("callback");
	}
}

package com.sk.skala.quizapi.tools;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.sk.skala.quizapi.data.batch.BatchJobOperation;
import com.sk.skala.quizapi.data.batch.BatchJobStatus;
import com.sk.skala.quizapi.data.common.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchJobThread extends Thread {

	RequestAttributes requestAttributes;
	JobLauncher jobLauncher;
	JobExplorer jobExplorer;
	BatchJobOperation batchJobOperation;
	Job job;

	RestTemplate restTemplate = new RestTemplate();

	public BatchJobThread(RequestAttributes requestAttributes, JobLauncher jobLauncher, JobExplorer jobExplorer,
			BatchJobOperation operation, Job job) {

		this.requestAttributes = requestAttributes;
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.batchJobOperation = operation;
		this.job = job;
	}

	public void run() {
		try {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			log.debug("BatchJobThread.run: start {}", batchJobOperation.toString());
			JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
			jobParametersBuilder.addLong("timestamp", System.currentTimeMillis());

			String offsets = BatchJobTool.convertOffsetsToString(batchJobOperation.getJobParameters());
			if (offsets != null) {
				jobParametersBuilder.addString("offsets", offsets);
			}

			String callback = null;
			if (batchJobOperation.getJobParameters() != null) {
				callback = (String) batchJobOperation.getJobParameters().get("callback");
				if (callback != null) {
					jobParametersBuilder.addString("callback", callback);
				}
			}
			jobLauncher.run(job, jobParametersBuilder.toJobParameters());
			if (callback != null) {
				BatchJobStatus batchJobStatus = getBatchJobStatus();
				restTemplate.postForEntity(callback, batchJobStatus, Response.class);
			}
		} catch (Exception e) {
			log.error("BatchJobThread.run: error {}", e.toString());
		} finally {
			RequestContextHolder.resetRequestAttributes();
			log.debug("BatchJobThread.run: end {}", batchJobOperation.toString());
		}
	}

	private BatchJobStatus getBatchJobStatus() {
		BatchJobStatus batchJobStatus = new BatchJobStatus(batchJobOperation);
		JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
		if (lastJobInstance != null) {
			JobExecution jobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
			batchJobStatus.setJobInstanceId(jobExecution.getJobId());
			batchJobStatus.setJobExecutionId(jobExecution.getId());
			batchJobStatus.setJobStartTime(StringTool.fromLocalDateTime(jobExecution.getStartTime()));
			batchJobStatus.setJobEndTime(StringTool.fromLocalDateTime(jobExecution.getEndTime()));
			batchJobStatus.setJobStatus(jobExecution.getStatus().toString());
			if (jobExecution.getExitStatus() != null) {
				batchJobStatus.setJobExitStatus(jobExecution.getExitStatus().getExitCode());
				batchJobStatus.setJobExitMessage(jobExecution.getExitStatus().getExitDescription());
			}
		}
		return batchJobStatus;
	}
}

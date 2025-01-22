package com.sk.skala.quizapi.tools;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.batch.BatchJobOperation;
import com.sk.skala.quizapi.data.batch.BatchJobStatus;
import com.sk.skala.quizapi.data.common.Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchJobHandler {

	private final JobOperator jobOperator;
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;

	public Response operate(String operation, BatchJobOperation jobOperation, Job job) throws Exception {
		switch (operation) {
		case BatchJobOperation.START:
			return startThread(jobOperation, job);

		case BatchJobOperation.STATUS:
			return status(jobOperation, job);

		case BatchJobOperation.STOP:
			return stop(jobOperation, job);

		case BatchJobOperation.ONDEMAND:
			return startThread(jobOperation, job);

		default:
			Response response = new Response();
			response.setError(Error.INVALID_JOB_OPERATION);
			return response;
		}
	}

	private Response startThread(BatchJobOperation jobOperation, Job job) throws Exception {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		BatchJobThread thread = new BatchJobThread(requestAttributes, jobLauncher, jobExplorer, jobOperation, job);
		thread.start();

		Thread.sleep(1500);
		return status(jobOperation, job);
	}

	private Response status(BatchJobOperation jobOperation, Job job) throws Exception {
		Response response = new Response();
		BatchJobStatus batchJobStatus = new BatchJobStatus(jobOperation);
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
			response.setBody(batchJobStatus);
		} else {
			response.setError(Error.INVALID_JOB_INSTANCE);
		}
		return response;
	}

	private Response stop(BatchJobOperation jobOperation, Job job) throws Exception {
		Response response = new Response();
		if (!isJobRunning(job)) {
			response.setError(Error.BATCH_JOB_IS_NOT_RUNNING);
			return response;
		}

		JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
		JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
		jobOperator.stop(lastJobExecution.getId());

		Thread.sleep(1500);
		return status(jobOperation, job);
	}

	private boolean isJobRunning(Job job) {
		JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
		if (lastJobInstance != null) {
			JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
			if (lastJobExecution.getStatus() == BatchStatus.STARTING
					|| lastJobExecution.getStatus() == BatchStatus.STARTED) {
				return true;
			}
		}
		return false;
	}
}

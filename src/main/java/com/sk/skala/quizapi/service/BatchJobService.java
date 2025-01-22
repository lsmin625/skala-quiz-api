package com.sk.skala.quizapi.service;

import org.springframework.batch.core.Job;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.data.batch.BatchJobOperation;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.tools.BatchJobHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchJobService {

	private final BatchJobHandler batchJobHandler;
	private final Job talHandoverJob;

	public Response doSampleDataFromFileJob(String operation, BatchJobOperation jobOperation) throws Exception {
		return batchJobHandler.operate(operation, jobOperation, talHandoverJob);
	}

}

package com.sk.skala.quizapi.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.batch.BatchJobOperation;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.service.BatchJobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/son/batch/api/")
public class BatchJobController {

	private final BatchJobService batchJobService;

	@PostMapping("/btal-ho/{operation}")
	public Response updateJob(@PathVariable String operation, @RequestBody BatchJobOperation batchJobOperation)
			throws Exception {
		return batchJobService.doSampleDataFromFileJob(operation, batchJobOperation);
	}

}

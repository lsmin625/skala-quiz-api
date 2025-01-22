package com.sk.skala.quizapi.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.springframework.web.client.RestTemplate;

import com.sk.skala.quizapi.config.Constant;
import com.sk.skala.quizapi.data.batch.BatchJobShell;
import com.sk.skala.quizapi.data.common.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchJobShellRunner extends Thread {
	BatchJobShell batchJobShell;
	RestTemplate restTemplate = new RestTemplate();

	public BatchJobShellRunner(BatchJobShell batchJobShell) {
		this.batchJobShell = batchJobShell;
	}

	@Override
	public void run() {
		batchJobShell.setJobStartTime(new Date());
		log.debug("BatchJobShellRunner.run: start {}", batchJobShell.toString());
		ProcessBuilder processBuilder = new ProcessBuilder(batchJobShell.getShell());
		BufferedReader bufferedReader = null;
		try {
			Process process = processBuilder.start();
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "euc-kr"));

			StringBuffer buff = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				buff.append(line + System.lineSeparator());
			}
			setJobResult(buff.toString());
		} catch (Exception e) {
			log.error("BatchJobShellRunner.run: error {}", e.toString());
			batchJobShell.setJobExitStatus(Constant.EXIT_STATUS_ERROR);
			batchJobShell.setJobExitMessage(e.toString());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (batchJobShell.getCallback() != null) {
				restTemplate.postForEntity(batchJobShell.getCallback(), batchJobShell, Response.class);
			}
			log.debug("BatchJobShellRunner.run: end {}", batchJobShell.toString());
		}
	}

	private void setJobResult(String text) {
		batchJobShell.setJobEndTime(new Date());
		batchJobShell.setJobExitStatus(BatchJobTool.findStringInText(text, Constant.JOB_EXIT_STATUS));
		batchJobShell.setJobExitMessage(BatchJobTool.findStringInText(text, Constant.JOB_EXIT_MESSAGE));
		batchJobShell.setJobItemsRead(BatchJobTool.findLongInText(text, Constant.JOB_ITEMS_READ));
		batchJobShell.setJobItemsWrite(BatchJobTool.findLongInText(text, Constant.JOB_ITEMS_WRITE));
	}
}

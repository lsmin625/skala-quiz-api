package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.AccountInfo;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.data.table.QuizReport;
import com.sk.skala.quizapi.data.table.QuizSummary;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.QuizReportRepository;
import com.sk.skala.quizapi.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizReportService {

	private final QuizRepository quizRepository;
	private final QuizReportRepository quizReportRepository;
	private final SessionHandler sessionHandler;

	public Response getQuizReportList(Long subjectId) throws Exception {
		AccountInfo account = sessionHandler.getAccountInfo();
		if (account == null) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		List<Quiz> quizList = quizRepository.findAllBySubjectId(subjectId);
		Map<Long, Quiz> quizMap = quizList.stream().collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

		List<QuizSummary> list = new ArrayList<>();
		List<QuizReport> quizReportList = quizReportRepository.findAllBySubjectId(subjectId);
		quizReportList.forEach(quizReport -> {
			Quiz quiz = quizMap.get(quizReport.getQuizId());
			list.add(new QuizSummary(quiz, quizReport));
		});

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);

		Response response = new Response();
		response.setBody(list);

		return response;
	}
}

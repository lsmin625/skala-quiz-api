package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.data.common.ExcelData;
import com.sk.skala.quizapi.data.common.ExcelData.Header;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.ApplicantQuiz;
import com.sk.skala.quizapi.data.table.ApplicantQuiz.QuizAnswer;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.repository.ApplicantQuizRepository;
import com.sk.skala.quizapi.repository.QuizRepository;
import com.sk.skala.quizapi.tools.ExcelTool;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicantQuizService {
	private static final String EXCEL_SHEET_NAME = "score";

	private final ApplicantQuizRepository applicantQuizRepository;
	private final QuizRepository quizRepository;

	public Response startQuiz(ApplicantQuiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getApplicantId(), item.getApplicantName())) {
			throw new ParameterException("ApplicantId", "ApplicantName");
		}

		Optional<ApplicantQuiz> option = applicantQuizRepository.findBySubjectIdAndApplicantId(item.getSubjectId(),
				item.getApplicantId());
		item.setStartTime(new Date());
		if (option.isEmpty()) {
			item.setId(null);
		} else {
			item.setId(option.get().getId());
		}
		applicantQuizRepository.save(item);

		return new Response();
	}

	public Response saveAnswer(ApplicantQuiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getApplicantId(), item.getApplicantName())) {
			throw new ParameterException("ApplicantId", "ApplicantName");
		}

		Optional<ApplicantQuiz> option = applicantQuizRepository.findBySubjectIdAndApplicantId(item.getSubjectId(),
				item.getApplicantId());
		if (option.isEmpty()) {
			item.setStartTime(new Date());
			item.setId(null);
		} else {
			item.setId(option.get().getId());
			item.setStartTime(option.get().getStartTime());
		}

		item.setFinishTime(new Date());
		item.setFinished(false);
		applicantQuizRepository.save(item);

		return new Response();
	}

	public Response submitAnswer(ApplicantQuiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getApplicantId(), item.getApplicantName())) {
			throw new ParameterException("ApplicantId", "ApplicantName");
		}

		Optional<ApplicantQuiz> option = applicantQuizRepository.findBySubjectIdAndApplicantId(item.getSubjectId(),
				item.getApplicantId());
		if (option.isEmpty()) {
			item.setStartTime(new Date());
			item.setId(null);
		} else {
			item.setId(option.get().getId());
			item.setStartTime(option.get().getStartTime());
		}

		item.setFinishTime(new Date());
		item.setFinished(true);
		applicantQuizRepository.save(item);

		return new Response();
	}

	@Transactional
	public Response scoreAnswers(Long subjectId) throws Exception {
		List<Quiz> quizList = quizRepository.findAllBySubjectId(subjectId);
		Map<Long, Quiz> quizMap = quizList.stream().collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

		List<ApplicantQuiz> applicantQuizzes = applicantQuizRepository.findAllBySubjectId(subjectId);

		for (ApplicantQuiz applicantQuiz : applicantQuizzes) {
			scoreApplicantQuiz(applicantQuiz, quizMap);
			applicantQuizRepository.save(applicantQuiz);
		}

		PagedList pagedList = new PagedList();
		pagedList.setTotal(applicantQuizzes.size());
		pagedList.setCount(applicantQuizzes.size());
		pagedList.setOffset(0);
		pagedList.setList(applicantQuizzes);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	private void scoreApplicantQuiz(ApplicantQuiz applicantQuiz, Map<Long, Quiz> quizMap) {
		applicantQuiz.setApplicantScore(0F);
		List<QuizAnswer> answers = applicantQuiz.getQuizAnswerList();

		for (QuizAnswer answer : answers) {
			Quiz quiz = quizMap.get(answer.getQuizId());
			if (quiz != null) {
				answer.setQuizAnswer(quiz.getQuizAnswer());
				answer.setQuizScore(quiz.getQuizScore());
				if (quiz.getQuizAnswer().toLowerCase().indexOf(answer.getApplicantAnswer().toLowerCase()) >= 0) {
					answer.setApplicantScore(quiz.getQuizScore());
					applicantQuiz.setApplicantScore(applicantQuiz.getApplicantScore() + quiz.getQuizScore());
				} else {
					answer.setApplicantScore(0F);
				}
			}
		}
		applicantQuiz.setQuizAnswerList(answers);
	}

	private List<Header> getExcelHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("과목ID", "subjectId"));
		headers.add(new Header("응시자ID", "applicantId"));
		headers.add(new Header("응사자명", "applicantName"));
		headers.add(new Header("시험시간", "startTime"));
		headers.add(new Header("제출시간", "finishTime"));
		headers.add(new Header("점수", "applicantScore"));
		return headers;
	}

	public Response getBySubject(Long subjectId) throws Exception {
		List<ApplicantQuiz> applicantQuizzes = applicantQuizRepository.findAllBySubjectId(subjectId);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(applicantQuizzes.size());
		pagedList.setCount(applicantQuizzes.size());
		pagedList.setOffset(0);
		pagedList.setList(applicantQuizzes);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response getByApplicant(String applicantId) throws Exception {
		List<ApplicantQuiz> applicantQuizzes = applicantQuizRepository.findAllByApplicantId(applicantId);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(applicantQuizzes.size());
		pagedList.setCount(applicantQuizzes.size());
		pagedList.setOffset(0);
		pagedList.setList(applicantQuizzes);

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public ResponseEntity<ByteArrayResource> buildExcelBySubject(Long subjectId) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<ApplicantQuiz> items = applicantQuizRepository.findAllBySubjectId(subjectId);
		excelData.setRows(items);

		return ExcelTool.build(excelData);
	}

	public ResponseEntity<ByteArrayResource> buildExcelByApplicant(String applicantId) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<ApplicantQuiz> items = applicantQuizRepository.findAllByApplicantId(applicantId);
		excelData.setRows(items);

		return ExcelTool.build(excelData);
	}
}

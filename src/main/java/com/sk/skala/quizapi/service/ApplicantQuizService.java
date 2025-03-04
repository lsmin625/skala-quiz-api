package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.ExcelData;
import com.sk.skala.quizapi.data.common.ExcelData.Header;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.ApplicantQuiz;
import com.sk.skala.quizapi.data.table.ApplicantQuiz.QuizAnswer;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.data.table.QuizReport;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.ApplicantQuizRepository;
import com.sk.skala.quizapi.repository.QuizReportRepository;
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
	private final QuizReportRepository quizReportRepository;

	public Response startQuiz(ApplicantQuiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getApplicantId(), item.getApplicantName())) {
			throw new ParameterException("ApplicantId", "ApplicantName");
		}

		Optional<ApplicantQuiz> option = applicantQuizRepository.findBySubjectIdAndApplicantId(item.getSubjectId(),
				item.getApplicantId());
		if (!option.isEmpty()) {
			ApplicantQuiz applicantQuiz = option.get();
			if (applicantQuiz.isFinished()) {
				throw new ResponseException(Error.APPLICANT_QUIZ_EXISTS);
			} else {
				item.setId(applicantQuiz.getId());
				item.setStartTime(applicantQuiz.getStartTime());
			}
		} else {
			item.setId(null);
			item.setStartTime(new Date());
		}

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

		Response response = new Response();
		response.setBody(getQuizAnswerList(item));
		return response;
	}

	private PagedList getQuizAnswerList(ApplicantQuiz applicantQuiz) {
		List<Quiz> quizList = quizRepository.findAllBySubjectId(applicantQuiz.getSubjectId());
		Map<Long, Quiz> quizMap = quizList.stream().collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

		scoreApplicantQuiz(applicantQuiz, quizMap);
		List<QuizAnswer> list = applicantQuiz.getQuizAnswerList();

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);
		return pagedList;
	}

	@Transactional
	public Response scoreAnswers(Long subjectId, String startDate) throws Exception {
		List<Quiz> quizList = quizRepository.findAllBySubjectId(subjectId);
		Map<Long, Quiz> quizMap = quizList.stream().collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

		List<ApplicantQuiz> applicantQuizzes = null;
		if (StringTool.isEmpty(startDate)) {
			quizReportRepository.deleteAllBySubjectId(subjectId);
			applicantQuizzes = applicantQuizRepository.findAllBySubjectId(subjectId);
		} else {
			quizReportRepository.deleteBySubjectIdAndScoreTime(subjectId, startDate);
			applicantQuizzes = applicantQuizRepository.findAllBySubjectIdAndStartTime(subjectId, startDate);
		}

		Map<Long, int[]> quizResultMap = new HashMap<>();

		for (ApplicantQuiz applicantQuiz : applicantQuizzes) {
			scoreApplicantQuiz(applicantQuiz, quizMap);

			for (QuizAnswer answer : applicantQuiz.getQuizAnswerList()) {
				Long quizId = answer.getQuizId();
				quizResultMap.putIfAbsent(quizId, new int[] { 0, 0 });
				boolean isCorrect = isValidAnswer(answer.getQuizAnswer(), answer.getApplicantAnswer());
				if (isCorrect) {
					quizResultMap.get(quizId)[0]++; // 정답자 수 증가
				} else {
					quizResultMap.get(quizId)[1]++; // 오답자 수 증가
				}
			}
			applicantQuizRepository.save(applicantQuiz);
		}

		updateQuizReports(subjectId, quizResultMap);

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
				if (isValidAnswer(quiz.getQuizAnswer(), answer.getApplicantAnswer())) {
					answer.setApplicantScore(quiz.getQuizScore());
					applicantQuiz.setApplicantScore(applicantQuiz.getApplicantScore() + quiz.getQuizScore());
				} else {
					answer.setApplicantScore(0F);
				}
			}
		}
		applicantQuiz.setQuizAnswerList(answers);
	}

	private boolean isValidAnswer(String quizAnswer, String applicantAnswer) {
		if (quizAnswer.equalsIgnoreCase(applicantAnswer)) {
			return true;
		} else {
			return false;
		}
	}

	private void updateQuizReports(Long subjectId, Map<Long, int[]> quizResultMap) {
		for (Map.Entry<Long, int[]> entry : quizResultMap.entrySet()) {
			Long quizId = entry.getKey();
			int correctCount = entry.getValue()[0];
			int incorrectCount = entry.getValue()[1];

			QuizReport quizReport = quizReportRepository.findBySubjectIdAndQuizId(subjectId, quizId)
					.orElse(new QuizReport(subjectId, quizId, 0, 0));

			quizReport.setCorrectCount(quizReport.getCorrectCount() + correctCount);
			quizReport.setIncorrectCount(quizReport.getIncorrectCount() + incorrectCount);

			quizReportRepository.save(quizReport);
		}
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

	public Response countApplicantBySubject(Long subjectId, String yyyymmdd) throws Exception {
		Long totalCount = applicantQuizRepository.countBySubjectId(subjectId);
		Long currentCount = applicantQuizRepository.countBySubjectIdForDate(subjectId, yyyymmdd);

		Map<String, Long> body = new HashMap<>();
		body.put("subjectId", subjectId);
		body.put("totalCount", totalCount);
		body.put("currentCount", currentCount);

		Response response = new Response();
		response.setBody(body);
		return response;
	}

	public Response countUnfinished(Long subjectId, String yyyymmdd) throws Exception {
		Long totalCount = applicantQuizRepository.countBySubjectId(subjectId);
		Long unfinishedCount = applicantQuizRepository.countUnfinishedForDate(subjectId, yyyymmdd);

		Map<String, Long> body = new HashMap<>();
		body.put("subjectId", subjectId);
		body.put("totalCount", totalCount);
		body.put("unfinishedCount", unfinishedCount);

		Response response = new Response();
		response.setBody(body);
		return response;
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

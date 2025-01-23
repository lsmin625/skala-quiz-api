package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.ExcelData;
import com.sk.skala.quizapi.data.common.ExcelData.Header;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.data.table.QuizDifficulty;
import com.sk.skala.quizapi.data.table.QuizExcel;
import com.sk.skala.quizapi.data.table.QuizType;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.QuizRepository;
import com.sk.skala.quizapi.tools.ExcelTool;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {
	private static final String EXCEL_SHEET_NAME = "quiz";

	private final QuizRepository quizRepository;

	public Response getQuizList(Long subjectId) throws Exception {
		List<Quiz> list = quizRepository.findAllBySubjectId(subjectId);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	public Response generateQuizzes(Long subjectId, Long high, Long medium, Long low) throws Exception {
		List<Quiz> allQuizzes = quizRepository.findAllBySubjectId(subjectId);

		if (allQuizzes.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		List<Quiz> highDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> quiz.getQuizDifficulty() == QuizDifficulty.HIGH).collect(Collectors.toList());
		List<Quiz> mediumDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> quiz.getQuizDifficulty() == QuizDifficulty.MEDIUM).collect(Collectors.toList());
		List<Quiz> lowDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> quiz.getQuizDifficulty() == QuizDifficulty.LOW).collect(Collectors.toList());

		List<Quiz> selectedQuizzes = new ArrayList<>();
		selectedQuizzes.addAll(selectRandomQuizzes(highDifficultyQuizzes, high));
		selectedQuizzes.addAll(selectRandomQuizzes(mediumDifficultyQuizzes, medium));
		selectedQuizzes.addAll(selectRandomQuizzes(lowDifficultyQuizzes, low));
		List<Quiz> shuffledQuizzes = selectRandomQuizzes(selectedQuizzes, selectedQuizzes.size() * 1L);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(shuffledQuizzes.size());
		pagedList.setCount(shuffledQuizzes.size());
		pagedList.setOffset(0);
		pagedList.setList(shuffledQuizzes);

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	private List<Quiz> selectRandomQuizzes(List<Quiz> quizzes, Long count) {
		if (quizzes.size() <= count) {
			return new ArrayList<>(quizzes);
		}
		Collections.shuffle(quizzes);
		return quizzes.subList(0, count.intValue());
	}

	public Response upsertQuiz(Quiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getQuizQuestion(), item.getQuizAnswer())) {
			throw new ParameterException("quizQuestion", "quizAnswer");
		}

		if (!isValidQuizDifficulty(item.getQuizDifficulty().toString())) {
			throw new ParameterException("quizDifficulty");
		}

		if (!isValidQuizType(item.getQuizType().toString())) {
			throw new ParameterException("quizType");
		}

		if (item.getSubject() == null || item.getSubject().getId() == 0) {
			throw new ParameterException("subjectId");
		}

		Optional<Quiz> option = quizRepository.findById(item.getId());
		if (option.isEmpty()) {
			item.setId(null);
		}

		quizRepository.save(item);
		return new Response();
	}

	public Response deleteQuiz(Quiz item) {
		if (quizRepository.existsById(item.getId())) {
			quizRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	private boolean isValidQuizDifficulty(String quizDifficulty) {
		try {
			QuizDifficulty.valueOf(quizDifficulty.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private boolean isValidQuizType(String quizType) {
		try {
			QuizType.valueOf(quizType.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private List<Header> getExcelHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("과목ID", "subjectId"));
		headers.add(new Header("질문", "quizQuestion"));
		headers.add(new Header("난이도-HIGH/MEDIUM/LOW", "quizDifficulty"));
		headers.add(new Header("질문유형-SINGLE/OPTION", "quizType"));
		headers.add(new Header("선다형옵션-구분(;)", "quizOptions"));
		headers.add(new Header("정답", "quizAnswer"));
		return headers;
	}

	public ResponseEntity<ByteArrayResource> buildTemplate() throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<List<Object>> rows = new ArrayList<List<Object>>();
		excelData.setRows(rows);
		return ExcelTool.build(excelData);
	}

	public Response parseExcel(Long subjectId, MultipartFile file) throws Exception {
		List<Header> headers = getExcelHeaders();
		List<QuizExcel> items = ExcelTool.parse(file, headers, QuizExcel.class);
		for (QuizExcel item : items) {
			item.setSubjectId(subjectId);
			upsertQuiz(QuizExcel.toQuiz(item));
		}
		return new Response();
	}

	public ResponseEntity<ByteArrayResource> buildExcel(Long subjectId) throws Exception {
		ExcelData excelData = new ExcelData();
		excelData.setSheetname(EXCEL_SHEET_NAME);
		excelData.setHeaders(getExcelHeaders());

		List<Quiz> items = quizRepository.findAllBySubjectId(subjectId);
		List<QuizExcel> list = new ArrayList<QuizExcel>();
		for (Quiz item : items) {
			list.add(QuizExcel.fromQuiz(item));
		}
		excelData.setRows(list);

		return ExcelTool.build(excelData);
	}

}

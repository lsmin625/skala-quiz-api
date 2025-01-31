package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import com.sk.skala.quizapi.data.table.QuizExcel;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.QuizRepository;
import com.sk.skala.quizapi.tools.ExcelTool;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {
	private static final String EXCEL_SHEET_NAME = "quiz";

	private final QuizRepository quizRepository;

	public Response getQuizList(Long subjectId) throws Exception {
		List<Quiz> list = quizRepository.findAllBySubjectId(subjectId);
		list.forEach(quiz -> quiz.getSubject().setInstructor(null));

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	@CacheEvict(value = "quizzes", key = "#subjectId")
	public Response clearCache(Long subjectId) {
		return new Response();
	}

	@Cacheable(value = "quizzes", key = "#subjectId")
	public Response generateQuizzes(Long subjectId, Long high, Long medium, Long low) throws Exception {
		List<Quiz> allQuizzes = quizRepository.findAllBySubjectId(subjectId);

		if (allQuizzes.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		List<Quiz> highDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> Quiz.DIFFICULTY_HIGH == quiz.getQuizDifficulty()).collect(Collectors.toList());
		List<Quiz> mediumDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> Quiz.DIFFICULTY_MEDIUM == quiz.getQuizDifficulty()).collect(Collectors.toList());
		List<Quiz> lowDifficultyQuizzes = allQuizzes.stream()
				.filter(quiz -> Quiz.DIFFICULTY_LOW == quiz.getQuizDifficulty()).collect(Collectors.toList());

		List<Quiz> selectedQuizzes = new ArrayList<>();
		selectedQuizzes.addAll(selectRandomQuizzes(highDifficultyQuizzes, high));
		selectedQuizzes.addAll(selectRandomQuizzes(mediumDifficultyQuizzes, medium));
		selectedQuizzes.addAll(selectRandomQuizzes(lowDifficultyQuizzes, low));

		List<Quiz> shuffledQuizzes = selectRandomQuizzes(selectedQuizzes, selectedQuizzes.size() * 1L);
		shuffledQuizzes.forEach(quiz -> quiz.setQuizAnswer(null));

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

	@Transactional
	public Response upsertQuiz(Quiz item) throws Exception {
		if (StringTool.isAnyEmpty(item.getQuizQuestion(), item.getQuizAnswer())) {
			throw new ParameterException("quizQuestion", "quizAnswer");
		}

		if (!isValidQuizDifficulty(item.getQuizDifficulty())) {
			throw new ParameterException("quizDifficulty");
		}

		if (!isValidQuizType(item.getQuizType())) {
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

	private boolean isValidQuizDifficulty(Integer quizDifficulty) {
		if (quizDifficulty == Quiz.DIFFICULTY_LOW || quizDifficulty == Quiz.DIFFICULTY_MEDIUM
				|| quizDifficulty == Quiz.DIFFICULTY_HIGH) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isValidQuizType(Integer quizType) {
		if (quizType == Quiz.TYPE_SINGLE || quizType == Quiz.TYPE_MULTIPLE) {
			return true;
		} else {
			return false;
		}
	}

	private List<Header> getExcelHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("질문", "quizQuestion"));
		headers.add(new Header("난이도-하(1)/중(2)/상(3)", "quizDifficulty"));
		headers.add(new Header("질문유형-단답(1)/선다(0)", "quizType"));
		headers.add(new Header("선다형옵션-구분(;)", "quizOptions"));
		headers.add(new Header("정답", "quizAnswer"));
		headers.add(new Header("배점-실수", "quizScore"));
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

	@Transactional
	public Response parseExcel(Long subjectId, MultipartFile file) throws Exception {
		List<Header> headers = getExcelHeaders();
		List<QuizExcel> items = ExcelTool.parse(file, headers, QuizExcel.class);
		if (items.size() > 0) {
			quizRepository.deleteAllBySubjectId(subjectId);
		}

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

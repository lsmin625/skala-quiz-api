package com.sk.skala.quizapi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.ApplicantQuiz;
import com.sk.skala.quizapi.data.table.ApplicantQuiz.QuizAnswer;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.repository.ApplicantQuizRepository;
import com.sk.skala.quizapi.repository.QuizRepository;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicantQuizService {
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
}

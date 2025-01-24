package com.sk.skala.quizapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.ApplicantQuiz;
import com.sk.skala.quizapi.service.ApplicantQuizService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applicant-quiz")
public class ApplicantQuizController {

	private final ApplicantQuizService applicantQuizService;

	@PostMapping("/start")
	public Response start(@RequestBody ApplicantQuiz item) throws Exception {
		return applicantQuizService.startQuiz(item);
	}

	@PostMapping("/save")
	public Response save(@RequestBody ApplicantQuiz item) throws Exception {
		return applicantQuizService.saveAnswer(item);
	}

	@PostMapping("/submit")
	public Response submit(@RequestBody ApplicantQuiz item) throws Exception {
		return applicantQuizService.submitAnswer(item);
	}

	@GetMapping("/score")
	public Response score(@RequestParam Long subjectId) throws Exception {
		return applicantQuizService.scoreAnswers(subjectId);
	}
}

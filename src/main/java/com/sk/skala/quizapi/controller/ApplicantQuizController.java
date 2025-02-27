package com.sk.skala.quizapi.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.ApplicantQuiz;
import com.sk.skala.quizapi.data.table.Subject;
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

	@PostMapping("/submit")
	public Response submit(@RequestBody ApplicantQuiz item) throws Exception {
		return applicantQuizService.submitAnswer(item);
	}

	@GetMapping("/unfinished")
	public Response unfinishe(@RequestParam Long subjectId, @RequestParam String date) throws Exception {
		return applicantQuizService.countUnfinished(subjectId, date);
	}

	@PostMapping("/score")
	public Response score(@RequestBody Subject item, @RequestParam(defaultValue = "") String date) throws Exception {
		return applicantQuizService.scoreAnswers(item.getId(), date);
	}

	@GetMapping("/subject")
	public Response getBySubject(@RequestParam Long subjectId) throws Exception {
		return applicantQuizService.getBySubject(subjectId);
	}

	@GetMapping("/applicant")
	public Response getByApplicant(@RequestParam String applicantId) throws Exception {
		return applicantQuizService.getByApplicant(applicantId);
	}

	@GetMapping("/applicant/count")
	public Response getApplicantCount(@RequestParam Long subjectId, @RequestParam String date) throws Exception {
		return applicantQuizService.countApplicantBySubject(subjectId, date);
	}

	@GetMapping("/excel/subject")
	public ResponseEntity<ByteArrayResource> excelSubject(@RequestParam Long subjectId) throws Exception {
		return applicantQuizService.buildExcelBySubject(subjectId);
	}

	@GetMapping("/excel/applicant")
	public ResponseEntity<ByteArrayResource> excelApplicant(@RequestParam String applicantId) throws Exception {
		return applicantQuizService.buildExcelByApplicant(applicantId);
	}
}

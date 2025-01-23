package com.sk.skala.quizapi.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Quiz;
import com.sk.skala.quizapi.service.QuizService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {

	private final QuizService quizService;

	@GetMapping("/list")
	public Response list(@RequestParam Long subjectId) throws Exception {
		return quizService.getQuizList(subjectId);
	}

	@GetMapping("/generate")
	public Response generate(@RequestParam Long subjectId, @RequestParam Long high, @RequestParam Long medium,
			@RequestParam Long low) throws Exception {
		return quizService.generateQuizzes(subjectId, high, medium, low);
	}

	@PostMapping
	public Response post(@RequestBody Quiz item) throws Exception {
		return quizService.upsertQuiz(item);
	}

	@DeleteMapping
	public Response delete(@RequestBody Quiz item) throws Exception {
		return quizService.deleteQuiz(item);
	}

	@GetMapping("/excel/download")
	public ResponseEntity<ByteArrayResource> excelDownload(@RequestParam Long subjectId) throws Exception {
		return quizService.buildExcel(subjectId);
	}

	@GetMapping("/excel/template")
	public ResponseEntity<ByteArrayResource> excelTemplate() throws Exception {
		return quizService.buildTemplate();
	}

	@PostMapping("/excel/upload")
	public Response excelUpload(@RequestParam Long subjectId, @RequestParam("file") MultipartFile file)
			throws Exception {
		return quizService.parseExcel(subjectId, file);
	}
}

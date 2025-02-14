package com.sk.skala.quizapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Subject;
import com.sk.skala.quizapi.service.SubjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subject")
public class SubjectController {

	private final SubjectService subjectService;

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count) throws Exception {
		return subjectService.getSubjectList(name, offset, count);
	}

	@GetMapping()
	public Response get() throws Exception {
		return subjectService.getSubjectsByInstructor();
	}

	@PostMapping
	public Response post(@RequestBody Subject item) throws Exception {
		return subjectService.upsertSubject(item);
	}

	@DeleteMapping
	public Response delete(@RequestBody Subject item) throws Exception {
		return subjectService.deleteSubject(item);
	}
}

package com.sk.skala.quizapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Instructor;
import com.sk.skala.quizapi.service.InstructorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {

	private final InstructorService instructorService;

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count) throws Exception {
		return instructorService.getInstructorList(name, offset, count);
	}

	@PostMapping
	public Response post(@RequestBody Instructor item) throws Exception {
		return instructorService.upsertInstructor(item);
	}

	@DeleteMapping
	public Response delete(@RequestBody Instructor item) throws Exception {
		return instructorService.deleteInstructor(item);
	}
}

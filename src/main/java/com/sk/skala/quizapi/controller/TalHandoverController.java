package com.sk.skala.quizapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.service.TalHandoverService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/son/api/btal-ho")
public class TalHandoverController {

	private final TalHandoverService talHandoverService;

	@GetMapping("/list")
	public Response list(@RequestParam String date, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count) throws Exception {
		return talHandoverService.findAll(date, offset, count);
	}

	@GetMapping()
	public Response get(@RequestParam Long talId, @RequestParam String date) throws Exception {
		return talHandoverService.findByTalId(talId, date);
	}
}

package com.sk.skala.quizapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.PldBase;
import com.sk.skala.quizapi.service.PldBaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/son/api/pld/base")
public class PldBaseController {

	private final PldBaseService pldBaseService;

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count, @RequestParam(defaultValue = "") String pldName)
			throws Exception {
		return pldBaseService.findAll(pldName, offset, count);
	}

	@GetMapping()
	public Response get(@RequestParam String pldName) throws Exception {
		return pldBaseService.find(pldName);
	}

	@PostMapping
	public Response post(@RequestBody PldBase data) throws Exception {
		return pldBaseService.save(data);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<PldBase> data) throws Exception {
		return pldBaseService.saveAll(data);
	}

	@DeleteMapping
	public Response delete(@RequestBody PldBase data) throws Exception {
		return pldBaseService.delete(data);
	}
}

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
import com.sk.skala.quizapi.data.table.PldGroup;
import com.sk.skala.quizapi.service.PldGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/son/api/pld/groups")
public class PldGroupController {

	private final PldGroupService pldGroupService;

	@GetMapping("/list")
	public Response list(@RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer count, @RequestParam String regionCode,
			@RequestParam(defaultValue = "") String groupName) throws Exception {
		return pldGroupService.findAll(regionCode, groupName, offset, count);
	}

	@PostMapping
	public Response post(@RequestBody PldGroup data) throws Exception {
		return pldGroupService.save(data);
	}

	@PostMapping("/bulk")
	public Response bulk(@RequestBody List<PldGroup> data) throws Exception {
		return pldGroupService.saveAll(data);
	}

	@DeleteMapping
	public Response delete(@RequestBody PldGroup data) throws Exception {
		return pldGroupService.delete(data);
	}
}

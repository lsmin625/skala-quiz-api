package com.sk.skala.quizapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Instructor;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.InstructorRepository;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorService {
	private final InstructorRepository instructorRepository;

	public Response getInstructorList(String name, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "instructorName"));
		Page<Instructor> paged = instructorRepository.findAllByInstructorNameLike(StringTool.like(name), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setOffset(paged.getNumber());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	public Response upsertInstructor(Instructor item) throws Exception {
		if (StringTool.isAnyEmpty(item.getInstructorEmail(), item.getInstructorName())) {
			throw new ParameterException("instructorEmail", "instructorName");
		}

		if (item.getId() > 0 && !instructorRepository.existsById(item.getId())) {
			item.setId(null);
		}

		instructorRepository.save(item);
		return new Response();
	}

	public Response deleteInstructor(Instructor item) {
		if (instructorRepository.existsById(item.getId())) {
			instructorRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}
}

package com.sk.skala.quizapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Subject;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.SubjectRepository;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {
	private final SubjectRepository subjectRepository;

	public Response getSubjectList(String name, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "subjectName"));
		Page<Subject> paged = subjectRepository.findAllBySubjectNameLike(StringTool.like(name), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setOffset(paged.getNumber());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	public Response getSubjectsByInstructor(Long id) throws Exception {
		List<Subject> list = subjectRepository.findAllByInstructorId(id);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	public Response upsertSubject(Subject item) throws Exception {
		if (StringTool.isAnyEmpty(item.getSubjectName())) {
			throw new ParameterException("subjectName");
		}

		if (item.getInstructor() == null || item.getInstructor().getId() == 0) {
			throw new ParameterException("instructorId");
		}

		Optional<Subject> option = subjectRepository.findById(item.getId());
		if (option.isEmpty()) {
			item.setId(null);
		}

		subjectRepository.save(item);
		return new Response();
	}

	public Response deleteSubject(Subject item) {
		if (subjectRepository.existsById(item.getId())) {
			subjectRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}
}

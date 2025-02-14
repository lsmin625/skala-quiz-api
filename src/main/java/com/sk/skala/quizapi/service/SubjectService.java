package com.sk.skala.quizapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.AccountInfo;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Subject;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.QuizRepository;
import com.sk.skala.quizapi.repository.SubjectRepository;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {
	private final SubjectRepository subjectRepository;
	private final SessionHandler sessionHandler;
	private final QuizRepository quizRepository;

	public Response getSubjectList(String name, int offset, int count) throws Exception {
		if (!sessionHandler.isAdmin()) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "subjectName"));
		Page<Subject> paged = subjectRepository.findAllBySubjectNameLike(StringTool.like(name), pageable);
		paged.getContent().forEach(subject -> subject.setInstructor(null));

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setOffset(paged.getNumber());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	public Response getSubjectsByInstructor() throws Exception {
		AccountInfo account = sessionHandler.getAccountInfo();
		if (account == null) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		List<Subject> list = subjectRepository.findAllByInstructorId(account.getInstructorId());
		list.forEach(subject -> subject.setInstructor(null));

		PagedList pagedList = new PagedList();
		pagedList.setTotal(list.size());
		pagedList.setCount(list.size());
		pagedList.setOffset(0);
		pagedList.setList(list);

		Response response = new Response();
		response.setBody(pagedList);

		return response;
	}

	@Transactional
	public Response upsertSubject(Subject item) throws Exception {

		if (StringTool.isAnyEmpty(item.getSubjectName())) {
			throw new ParameterException("subjectName");
		}

		if (item.getInstructor() == null) {
			throw new ParameterException("instructorId");
		}

		AccountInfo account = sessionHandler.getAccountInfo();
		if (account == null) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		item.getInstructor().setId(account.getInstructorId());

		Optional<Subject> option = subjectRepository.findById(item.getId());
		if (option.isEmpty()) {
			item.setId(null);
		}
		subjectRepository.save(item);

		List<Subject> list = subjectRepository.findAllByInstructorId(item.getInstructor().getId());
		sessionHandler.storeAccessToken(account, list);

		return new Response();
	}

	@Transactional
	public Response deleteSubject(Subject item) {
		AccountInfo account = sessionHandler.getAccountInfo();
		if (account == null || account.getInstructorId() != item.getInstructor().getId()) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		if (subjectRepository.existsById(item.getId())) {
			quizRepository.deleteAllBySubjectId(item.getId());
			subjectRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		List<Subject> list = subjectRepository.findAllByInstructorId(item.getInstructor().getId());
		sessionHandler.storeAccessToken(account, list);

		return new Response();
	}
}

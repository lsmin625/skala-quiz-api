package com.sk.skala.quizapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.quizapi.config.Constant;
import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.AccountInfo;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.Instructor;
import com.sk.skala.quizapi.data.table.Subject;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.InstructorRepository;
import com.sk.skala.quizapi.repository.SubjectRepository;
import com.sk.skala.quizapi.tools.JwtTool;
import com.sk.skala.quizapi.tools.SecureTool;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorService {
	private final InstructorRepository instructorRepository;
	private final SubjectRepository subjectRepository;
	private final SessionHandler sessionHandler;

	public Response getInstructorList(String name, int offset, int count) throws Exception {
		if (!sessionHandler.isAdmin()) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

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

	public Response checkInstructor(Instructor item) throws Exception {
		if (StringTool.isAnyEmpty(item.getInstructorEmail(), item.getInstructorPassword())) {
			throw new ParameterException("instructorEmail", "instructorPassword");
		}

		Optional<Instructor> option = instructorRepository.findByInstructorEmail(item.getInstructorEmail());
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		Instructor instructor = option.get();
		String password = SecureTool.decryptAes(instructor.getInstructorPassword());
		if (!password.equals(item.getInstructorPassword())) {
			throw new ResponseException(Error.INVALID_ID_OR_PASSWORD);
		}

		AccountInfo account = storeAccessToken(instructor);

		Response response = new Response();
		response.setBody(account);

		return response;
	}

	public Response upsertInstructor(Instructor item) throws Exception {

		if (StringTool.isAnyEmpty(item.getInstructorEmail(), item.getInstructorName(), item.getInstructorPassword())) {
			throw new ParameterException("instructorEmail", "instructorName", "instructorPassword");
		}

		AccountInfo account = sessionHandler.getAccountInfo();
		if (account == null || !account.getAccountId().equalsIgnoreCase(item.getInstructorEmail())) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		Optional<Instructor> option = instructorRepository.findByInstructorEmail(item.getInstructorEmail());
		if (option.isEmpty()) {
			item.setId(null);
		} else {
			item.setId(option.get().getId());
		}

		item.setInstructorPassword(SecureTool.encryptAes(item.getInstructorPassword()));
		instructorRepository.save(item);
		return new Response();
	}

	public Response deleteInstructor(Instructor item) {
		if (!sessionHandler.isAdmin()) {
			throw new ResponseException(Error.NOT_AUTHORIZED);
		}

		if (instructorRepository.existsById(item.getId())) {
			instructorRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

	public AccountInfo storeAccessToken(Instructor instructor) {
		AccountInfo account = new AccountInfo(instructor.getInstructorEmail(), instructor.getInstructorName(),
				AccountInfo.ROLE_WRITER);

		List<Subject> subjects = subjectRepository.findAllByInstructorId(instructor.getId());
		List<Long> subjectIds = new ArrayList<>();
		for (Subject subject : subjects) {
			subjectIds.add(subject.getId());
		}
		account.setSubjectIds(subjectIds);

		String token = JwtTool.generateToken(account.getAccountId(), account, Constant.JWT_SECRET_BFF);
		Cookie cookie = new Cookie(Constant.JWT_ACCESS_COOKIE, token);
		cookie.setMaxAge(Constant.JWT_ACCESS_TTL);
		cookie.setPath("/");
		cookie.setSecure(false);

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.getResponse().addCookie(cookie);

		return account;
	}

}

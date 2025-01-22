package com.sk.skala.quizapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.TalHandover;
import com.sk.skala.quizapi.repository.TalHandoverRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TalHandoverService {

	private final TalHandoverRepository talHandoverRepository;

	public Response findAll(String date, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count);
		Page<TalHandover> paged = talHandoverRepository.findAllByCollectedDateString(date, pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response findByTalId(Long talId, String date) throws Exception {
		Optional<TalHandover> option = talHandoverRepository.findByTalIdAndCollectedDateString(talId, date);

		Response response = new Response();
		response.setBody(option.get());
		return response;
	}
}

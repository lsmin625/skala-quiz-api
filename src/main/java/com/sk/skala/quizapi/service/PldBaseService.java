package com.sk.skala.quizapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.PldBase;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.PldBaseRepository;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PldBaseService {

	private final PldBaseRepository pldBaseRepository;

	public Response findAll(String pldName, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count);
		Page<PldBase> paged = pldBaseRepository.findAllByPldNameLike(StringTool.like(pldName), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response find(String pldName) throws Exception {
		Optional<PldBase> option = pldBaseRepository.findByPldName(pldName);

		Response response = new Response();
		response.setBody(option.get());
		return response;
	}

	public Response save(PldBase item) throws Exception {
		if (StringTool.isAnyEmpty(item.getPldName())) {
			throw new ParameterException("pldName");
		}

		if (item.getId() > 0 && !pldBaseRepository.existsById(item.getId())) {
			item.setId(null);
		}

		pldBaseRepository.save(item);
		return new Response();
	}

	public Response saveAll(List<PldBase> items) throws Exception {
		for (PldBase item : items) {
			if (StringTool.isAnyEmpty(item.getPldName())) {
				throw new ParameterException("pldName", "pldPara");
			}

			if (item.getId() > 0 && !pldBaseRepository.existsById(item.getId())) {
				item.setId(null);
			}
		}

		pldBaseRepository.saveAll(items);
		return new Response();
	}

	public Response delete(PldBase item) throws Exception {
		if (pldBaseRepository.existsById(item.getId())) {
			pldBaseRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

}

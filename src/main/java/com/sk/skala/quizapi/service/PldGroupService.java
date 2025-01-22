package com.sk.skala.quizapi.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.PagedList;
import com.sk.skala.quizapi.data.common.Response;
import com.sk.skala.quizapi.data.table.PldGroup;
import com.sk.skala.quizapi.exception.ParameterException;
import com.sk.skala.quizapi.exception.ResponseException;
import com.sk.skala.quizapi.repository.PldGroupRepository;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PldGroupService {

	private final PldGroupRepository pldGroupRepository;

	public Response findAll(String regionCode, String groupName, int offset, int count) throws Exception {
		Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Direction.ASC, "groupName"));
		Page<PldGroup> paged = pldGroupRepository.findAllByRegionCodeAndGroupNameLike(regionCode,
				StringTool.like(groupName), pageable);

		PagedList pagedList = new PagedList();
		pagedList.setTotal(paged.getTotalElements());
		pagedList.setOffset(pageable.getPageNumber());
		pagedList.setCount(paged.getNumberOfElements());
		pagedList.setList(paged.getContent());

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	public Response save(PldGroup item) throws Exception {
		if (StringTool.isAnyEmpty(item.getRegionCode(), item.getServiceType(), item.getGroupName())) {
			throw new ParameterException("regionCode", "serviceType", "groupName");
		}

		if (item.getId() > 0 && !pldGroupRepository.existsById(item.getId())) {
			item.setId(null);
		}

		pldGroupRepository.save(item);
		return new Response();
	}

	public Response saveAll(List<PldGroup> items) throws Exception {
		for (PldGroup item : items) {
			if (StringTool.isAnyEmpty(item.getRegionCode(), item.getServiceType(), item.getGroupName())) {
				throw new ParameterException("regionCode", "serviceType", "groupName");
			}

			if (item.getId() > 0 && !pldGroupRepository.existsById(item.getId())) {
				item.setId(null);
			}
		}

		pldGroupRepository.saveAll(items);
		return new Response();
	}

	public Response delete(PldGroup item) throws Exception {
		if (pldGroupRepository.existsById(item.getId())) {
			pldGroupRepository.deleteById(item.getId());
		} else {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}
		return new Response();
	}

}

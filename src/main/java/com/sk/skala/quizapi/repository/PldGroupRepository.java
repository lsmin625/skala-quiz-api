package com.sk.skala.quizapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.PldGroup;

@Repository
public interface PldGroupRepository extends JpaRepository<PldGroup, Long> {
	Page<PldGroup> findAllByRegionCodeAndGroupNameLike(String code, String group, Pageable pageable);

	Page<PldGroup> findAllByGroupNameLike(String group, Pageable pageable);
}
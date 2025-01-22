package com.sk.skala.quizapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.PldBase;

@Repository
public interface PldBaseRepository extends JpaRepository<PldBase, Long> {
	Page<PldBase> findAllByPldNameLike(String name, Pageable pageable);

	Optional<PldBase> findByPldName(String name);
}
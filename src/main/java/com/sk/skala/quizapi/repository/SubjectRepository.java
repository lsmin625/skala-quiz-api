package com.sk.skala.quizapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

	Page<Subject> findAllBySubjectNameLike(String name, Pageable pageable);

	List<Subject> findAllByInstructorId(Long id);
}
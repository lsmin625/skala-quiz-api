package com.sk.skala.quizapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

	Page<Instructor> findAllByInstructorNameLike(String name, Pageable pageable);

	Optional<Instructor> findByInstructorEmail(String email);
}

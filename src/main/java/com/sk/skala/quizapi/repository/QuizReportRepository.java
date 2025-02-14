package com.sk.skala.quizapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.QuizReport;

@Repository
public interface QuizReportRepository extends JpaRepository<QuizReport, Long> {

	List<QuizReport> findAllBySubjectId(Long id);

	Optional<QuizReport> findBySubjectIdAndQuizId(Long subjectId, Long quizId);

	void deleteAllBySubjectId(Long id);

}

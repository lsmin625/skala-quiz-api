package com.sk.skala.quizapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.QuizReport;

import jakarta.transaction.Transactional;

@Repository
public interface QuizReportRepository extends JpaRepository<QuizReport, Long> {

	List<QuizReport> findAllBySubjectId(Long id);

	@Query(value = "SELECT * FROM skala_quiz_report WHERE subject_id = :subjectId AND DATE_FORMAT(score_time, '%Y%m%d') = :yyyymmdd", nativeQuery = true)
	List<QuizReport> findAllBySubjectIdAndScoreTime(Long subjectId, String yyyymmdd);

	Optional<QuizReport> findBySubjectIdAndQuizId(Long subjectId, Long quizId);

	void deleteAllBySubjectId(Long id);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM skala_quiz_report WHERE subject_id = :subjectId AND DATE_FORMAT(score_time, '%Y%m%d') = :yyyymmdd", nativeQuery = true)
	void deleteBySubjectIdAndScoreTime(Long subjectId, String yyyymmdd);
}

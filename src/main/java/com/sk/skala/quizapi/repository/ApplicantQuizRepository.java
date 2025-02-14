package com.sk.skala.quizapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.ApplicantQuiz;

@Repository
public interface ApplicantQuizRepository extends JpaRepository<ApplicantQuiz, Long> {
	Optional<ApplicantQuiz> findBySubjectIdAndApplicantId(Long subjectId, String applicantId);

	List<ApplicantQuiz> findAllBySubjectId(Long subjectId);

	List<ApplicantQuiz> findAllByApplicantId(String applicantId);

	Long countBySubjectId(Long subjectId);
}

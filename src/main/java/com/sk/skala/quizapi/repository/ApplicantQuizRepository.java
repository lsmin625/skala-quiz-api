package com.sk.skala.quizapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.ApplicantQuiz;

@Repository
public interface ApplicantQuizRepository extends JpaRepository<ApplicantQuiz, Long> {
	Optional<ApplicantQuiz> findBySubjectIdAndApplicantId(Long subjectId, String applicantId);

	List<ApplicantQuiz> findAllBySubjectId(Long subjectId);

	List<ApplicantQuiz> findAllByApplicantId(String applicantId);

	Long countBySubjectId(Long subjectId);

	@Query(value = "SELECT COUNT(*) FROM skala_applicant_quiz WHERE finished = 0 AND subject_id = :subjectId AND DATE_FORMAT(start_time, '%Y%m%d') = :yyyymmdd", nativeQuery = true)
	Long countUnfinishedForDate(@Param("subjectId") Long subjectId, @Param("yyyymmdd") String yyyymmdd);

	@Query(value = "SELECT COUNT(*) FROM skala_applicant_quiz WHERE subject_id = :subjectId AND DATE_FORMAT(start_time, '%Y%m%d') = :yyyymmdd", nativeQuery = true)
	Long countBySubjectIdForDate(@Param("subjectId") Long subjectId, @Param("yyyymmdd") String yyyymmdd);

	@Query(value = "SELECT * FROM skala_applicant_quiz WHERE subject_id = :subjectId AND DATE_FORMAT(start_time, '%Y%m%d') = :yyyymmdd", nativeQuery = true)
	List<ApplicantQuiz> findAllBySubjectIdAndStartTime(@Param("subjectId") Long subjectId,
			@Param("yyyymmdd") String yyyymmdd);

}

package com.sk.skala.quizapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.TalHandover;
import com.sk.skala.quizapi.data.table.TalHandoverId;

@Repository
public interface TalHandoverRepository extends JpaRepository<TalHandover, TalHandoverId> {

	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM TalHandover s WHERE FUNCTION('DATE_FORMAT', s.id.collectedDate, '%Y%m%d') = ?1")
	boolean existsByCollectedDateString(String collectedDateString);

	@Query("SELECT s FROM TalHandover s WHERE FUNCTION('DATE_FORMAT', s.id.collectedDate, '%Y%m%d') = ?1")
	Page<TalHandover> findAllByCollectedDateString(String date, Pageable pageable);

	@Query("SELECT s FROM TalHandover s WHERE s.id.talId = ?1 AND FUNCTION('DATE_FORMAT', s.id.collectedDate, '%Y%m%d') = ?2")
	Optional<TalHandover> findByTalIdAndCollectedDateString(Long talId, String date);
}
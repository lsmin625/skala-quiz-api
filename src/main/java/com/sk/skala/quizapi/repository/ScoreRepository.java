package com.sk.skala.quizapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.Score;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
}

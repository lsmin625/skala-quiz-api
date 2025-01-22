package com.sk.skala.quizapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sk.skala.quizapi.data.table.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
}

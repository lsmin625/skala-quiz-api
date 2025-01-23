package com.sk.skala.quizapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "skala_quiz")
public class Quiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String quizQuestion;

	@Enumerated(EnumType.STRING)
	private Difficulty quizDifficulty;

	@Enumerated(EnumType.STRING)
	private QuestionType quizType;

	private String quizOptions;

	private String quizAnswer;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

	// Getters and Setters
}

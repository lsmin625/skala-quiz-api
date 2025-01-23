package com.sk.skala.quizapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "skala_applicant_quiz")
public class ApplicantQuiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String applicantId;

	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;

	private String applicantAnswer;

	private boolean isFinalSubmission;

}

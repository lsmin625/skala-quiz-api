package com.sk.skala.quizapi.data.table;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "skala_quiz_report")
public class QuizReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long subjectId;
	private Long quizId;
	private int correctCount;
	private int incorrectCount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date scoreTime;

	public QuizReport() {
	}

	public QuizReport(Long subjectId, Long quizId, int correctCount, int incorrectCount) {
		this.id = null;
		this.subjectId = subjectId;
		this.quizId = quizId;
		this.correctCount = correctCount;
		this.incorrectCount = incorrectCount;
		this.scoreTime = new Date();
	}
}

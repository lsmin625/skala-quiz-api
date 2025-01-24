package com.sk.skala.quizapi.data.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sk.skala.quizapi.tools.JsonTool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "skala_applicant_quiz")
public class ApplicantQuiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long subjectId;
	private String applicantId;
	private String applicantName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date finishTime;

	@JsonIgnore
	private String quizAnswers;

	private Float applicantScore;

	private boolean finished;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class QuizAnswer {
		Long quizId;
		String quizQuestion;
		String quizAnswer;
		Float quizScore;
		String applicantAnswer;
		Float applicantScore;
	}

	public List<QuizAnswer> getQuizAnswerList() {
		if (quizAnswers != null) {
			return JsonTool.toList(quizAnswers, QuizAnswer.class);
		} else {
			return new ArrayList<QuizAnswer>();
		}
	}

	public void setQuizAnswerList(List<QuizAnswer> list) {
		quizAnswers = JsonTool.toString(list);
	}
}

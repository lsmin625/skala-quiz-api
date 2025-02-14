package com.sk.skala.quizapi.data.table;

import lombok.Data;

@Data
public class QuizSummary {

	private Long subjectId;
	private String subjectName;
	private Long quizId;
	private String quizQuestion;
	private int correctCount;
	private int incorrectCount;

	public QuizSummary() {
	}

	public QuizSummary(Quiz quiz, QuizReport report) {
		this.subjectName = quiz.getSubject().getSubjectName();
		this.quizQuestion = quiz.getQuizQuestion();
		this.subjectId = report.getSubjectId();
		this.quizId = report.getQuizId();
		this.correctCount = report.getCorrectCount();
		this.incorrectCount = report.getIncorrectCount();
	}
}

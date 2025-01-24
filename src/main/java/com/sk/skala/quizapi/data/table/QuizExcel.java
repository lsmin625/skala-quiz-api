package com.sk.skala.quizapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.sk.skala.quizapi.tools.StringTool;

import lombok.Data;

@Data
public class QuizExcel {
	private Long id;
	private Long subjectId;
	private String quizQuestion;
	private Integer quizDifficulty;
	private Integer quizType;
	private String quizOptions;
	private String quizAnswer;
	private Float quizScore;

	public List<String> getQuizOptionList() {
		List<String> list = new ArrayList<String>();
		if (!StringTool.isEmpty(quizOptions)) {
			String[] options = quizOptions.split(";");
			for (String option : options) {
				list.add(option.trim());
			}
		}
		return list;
	}

	public void setQuizOptionList(List<String> list) {
		if (list != null && !list.isEmpty()) {
			quizOptions = String.join(";", list);
		} else {
			quizOptions = "";
		}
	}

	public static QuizExcel fromQuiz(Quiz quiz) {
		QuizExcel quizExcel = new QuizExcel();
		quizExcel.id = quiz.getId();
		quizExcel.subjectId = quiz.getSubject() != null ? quiz.getSubject().getId() : null;
		quizExcel.quizQuestion = quiz.getQuizQuestion();
		quizExcel.quizDifficulty = quiz.getQuizDifficulty();
		quizExcel.quizType = quiz.getQuizType();
		quizExcel.quizOptions = quiz.getQuizOptions(); // JSON 문자열 그대로 설정
		quizExcel.quizAnswer = quiz.getQuizAnswer();
		quizExcel.quizScore = quiz.getQuizScore();
		return quizExcel;
	}

	public static Quiz toQuiz(QuizExcel quizExcel) {
		Quiz quiz = new Quiz();
		quiz.setId(0L);
		quiz.setQuizQuestion(quizExcel.getQuizQuestion());
		quiz.setQuizDifficulty(quizExcel.getQuizDifficulty());
		quiz.setQuizType(quizExcel.getQuizType());
		quiz.setQuizOptions(quizExcel.getQuizOptions()); // JSON 문자열 그대로 설정
		quiz.setQuizAnswer(quizExcel.getQuizAnswer());
		quiz.setQuizScore(quizExcel.getQuizScore());
		if (quizExcel.getSubjectId() != null) {
			Subject subject = new Subject();
			subject.setId(quizExcel.getSubjectId());
			quiz.setSubject(subject);
		}
		return quiz;
	}
}

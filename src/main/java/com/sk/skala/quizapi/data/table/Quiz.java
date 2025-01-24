package com.sk.skala.quizapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.skala.quizapi.tools.StringTool;

import jakarta.persistence.Entity;
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

	public static final Integer DIFFICULTY_HIGH = 3;
	public static final Integer DIFFICULTY_MEDIUM = 2;
	public static final Integer DIFFICULTY_LOW = 1;

	public static final Integer TYPE_SINGLE = 1;
	public static final Integer TYPE_MULTIPLE = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String quizQuestion;

	private Integer quizDifficulty;

	private Integer quizType;

	@JsonIgnore
	private String quizOptions;

	private String quizAnswer;

	private Float quizScore;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

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
}

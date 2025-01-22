package com.sk.skala.quizapi.simulator;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class AmosResponse {
	String title;
	private List<Field> fields = new ArrayList<>();
	private List<List<String>> rows = new ArrayList<>();
	private int dashSize = 0;

	public void addField(String name, int size) {
		fields.add(new Field(name, size));
		this.dashSize += size;
	}

	public void addRow(String... columns) {
		List<String> row = new ArrayList<>();
		for (String column : columns) {
			row.add(column);
		}
		this.rows.add(row);
	}

	public String printResponse() {
		StringBuilder sb = new StringBuilder();

		sb.append(title);
		sb.append(TextLine.EOL);
		sb.append(TextLine.drawDash(dashSize));
		sb.append(TextLine.EOL);

		for (Field field : fields) {
			sb.append(TextLine.appendSpace(field.getSize(), field.getName()));
		}
		sb.append(TextLine.EOL);

		sb.append(TextLine.drawDash(dashSize));
		sb.append(TextLine.EOL);

		for (List<String> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				sb.append(TextLine.appendSpace(fields.get(i).getSize(), row.get(i)));
			}
			sb.append(TextLine.EOL);
		}

		sb.append(TextLine.drawDash(dashSize));

		return sb.toString();
	}

	@Data
	class Field {
		String name;
		int size;

		Field(String name, int size) {
			this.name = name;
			this.size = size;
		}
	}

	AmosResponse() {
		setupTitle();
		setupHeader();
		setupBody();
	}

	void setupTitle() {
	}

	void setupHeader() {
	}

	void setupBody() {
	}
}

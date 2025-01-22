package com.sk.skala.quizapi.simulator;

public class AmosResponseStCell extends AmosResponse {

	@Override
	void setupTitle() {
		title = "CELL INFORMATION:";
	}

	@Override
	void setupHeader() {
		addField("CELL_ID", 12);
		addField("STATUS", 16);
		addField("TYPE", 12);
		addField("FREQUENCY", 12);
		addField("POWER", 12);
		addField("ALARM", 12);
	}

	@Override
	void setupBody() {
		addRow("CELL001", "ACTIVE", "MACRO", "900 MHz", "20 W", "NONE");
		addRow("CELL002", "ACTIVE", "MACRO", "900 MHz", "20 W", "NONE");
		addRow("CELL003", "INACTIVE", "MICRO", "1800 MHz", "10 W", "MAJOR");
		addRow("CELL004", "ACTIVE", "MICRO", "900 MHz", "15 W", "NONE");
		addRow("CELL005", "MAINTENANCE", "PICO", "2100 MHz", "5 W", "NONE");
	}
}

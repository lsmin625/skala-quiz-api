package com.sk.skala.quizapi.simulator;

public class AmosResponseLtAll extends AmosResponse {

	@Override
	void setupTitle() {
		title = "LINK TERMINAL INFORMATION:";
	}

	@Override
	void setupHeader() {
		addField("LINK_ID", 12);
		addField("STATUS", 16);
		addField("TYPE", 12);
		addField("BANDWIDTH", 16);
		addField("UTILIZATION", 16);
		addField("ALARM", 12);
	}

	@Override
	void setupBody() {
		addRow("LT001", "ACTIVE", "ETHERNET", "1 Gbps", "60%", "NONE");
		addRow("LT002", "ACTIVE", "E1", "1 Gbps", "80%", "MINOR");
		addRow("LT003", "INACTIVE", "STM-1", "2 Gbps", "0%", "CRITICAL");
		addRow("LT004", "ACTIVE", "MICROWAVE", "155 Mbps", "45%", "NONE");
		addRow("LT005", "MAINTENANCE", "ETHERNET", "50 Mbps", "25%", "NONE");
	}
}

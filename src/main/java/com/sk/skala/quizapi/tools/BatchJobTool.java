package com.sk.skala.quizapi.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Data;

public class BatchJobTool {
	private static final String INDEX_NAME = "offsets";
	private static final String INDEX_START = "start";
	private static final String INDEX_END = "end";

	@SuppressWarnings("unchecked")
	public static String convertOffsetsToString(Map<String, Object> jobParameters) {
		List<Map<String, String>> offsets = (List<Map<String, String>>) jobParameters.get(INDEX_NAME);
		if (offsets != null && offsets.size() > 0) {
			return offsets.stream().map(offset -> offset.get(INDEX_START) + "-" + offset.get(INDEX_END))
					.collect(Collectors.joining(","));
		} else {
			return null;
		}
	}

	public static List<Offset> convertStringToOffsets(String offsetString) {
		List<Offset> list = new ArrayList<>();
		if (!StringTool.isEmpty(offsetString)) {
			String[] offsetPairs = offsetString.split(",");
			for (String offsetPair : offsetPairs) {
				String[] startEnd = offsetPair.split("-");
				if (startEnd.length > 1) {
					Offset offset = new Offset(startEnd[0], startEnd[1]);
					list.add(offset);
				}
			}
		}
		return list;
	}

	@Data
	public static class Offset {
		String start;
		String end;

		Offset(String start, String end) {
			this.start = start;
			this.end = end;
		}
	}

	public static String findStringInText(String text, String key) {
		String regex = key + "\\s*[:=]\\s*(.+?)\\s*(?=\r?$|\n)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static Long findLongInText(String text, String key) {
		String regex = key + "\\s*[:=]\\s*(\\S+)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}
		return null;
	}
}

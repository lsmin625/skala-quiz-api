package com.sk.skala.quizapi.simulator;

public class TextLine {
	public static final String EOL = System.lineSeparator();

	private static final String SPACE = " ";
	private static final String ZERO = "0";
	private static final String DASH = "-";

	private static String space(int margin) {
		return SPACE.repeat(Math.max(0, margin));
	}

	public static String insertSpace(int size, String orig) {
		if (orig == null)
			orig = "";
		int padding = Math.max(0, size - orig.getBytes().length);
		return space(padding) + orig;
	}

	public static String insertSpace(int size, Integer orig) {
		return insertSpace(size, Integer.toString(orig));
	}

	public static String insertSpace(int size, Long orig) {
		return insertSpace(size, Long.toString(orig));
	}

	public static String appendSpace(int size, String orig) {
		if (orig == null)
			orig = "";
		int padding = Math.max(0, size - orig.getBytes().length);
		return orig + space(padding);
	}

	public static String appendSpace(int size, Integer orig) {
		return appendSpace(size, Integer.toString(orig));
	}

	public static String appendSpace(int size, Long orig) {
		return appendSpace(size, Long.toString(orig));
	}

	public static String drawDash(int size) {
		return DASH.repeat(Math.max(0, size));
	}

	public static String drawDash(String head) {
		int size = head.indexOf(EOL);
		if (size < 0)
			size = head.length();
		return drawDash(size);
	}

	public static String setIndent(int margin, String orig) {
		if (orig == null || orig.isEmpty())
			return "";
		String[] lines = orig.split("\\R");
		StringBuilder builder = new StringBuilder();
		String prefix = space(margin);
		for (String line : lines) {
			builder.append(prefix).append(line).append(EOL);
		}
		return builder.toString();
	}

	private static String zero(int margin) {
		return ZERO.repeat(Math.max(0, margin));
	}

	public static String insertZero(int size, String orig) {
		if (orig == null)
			orig = "";
		int padding = Math.max(0, size - orig.getBytes().length);
		return zero(padding) + orig;
	}

	public static String insertZero(int size, Integer orig) {
		return insertZero(size, Integer.toString(orig));
	}

	public static String insertZero(int size, Long orig) {
		return insertZero(size, Long.toString(orig));
	}

	public static String appendZero(int size, String orig) {
		if (orig == null)
			orig = "";
		int padding = Math.max(0, size - orig.getBytes().length);
		return orig + zero(padding);
	}

	public static String appendZero(int size, Integer orig) {
		return appendZero(size, Integer.toString(orig));
	}

	public static String appendZero(int size, Long orig) {
		return appendZero(size, Long.toString(orig));
	}
}

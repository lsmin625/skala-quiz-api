package com.sk.skala.quizapi.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.sk.skala.quizapi.data.common.ExcelData.Header;

public class CsvFileTool {

	private static final String CONTENT_TYPE = "text/csv";

	public static void writeHeaders(PrintWriter writer, List<Header> headers) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < headers.size(); i++) {
			list.add(headers.get(i).getName());
		}
		writer.println(escapeCsv(list));
	}

	public static void writeRows(PrintWriter writer, List<Header> headers, List<List<Object>> rows) {
		for (List<Object> row : rows) {
			writer.println(escapeCsv(row.stream().map(Object::toString).collect(Collectors.toList())));
		}
	}

	public static ResponseEntity<ByteArrayResource> buildCsv(String filename, List<Header> headers,
			Consumer<PrintWriter> dataWriter) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

			writeHeaders(writer, headers);

			dataWriter.accept(writer);

			writer.flush();

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
					.body(new ByteArrayResource(out.toByteArray()));
		}
	}

	private static String escapeCsv(List<String> values) {
		return values.stream().map(value -> {
			if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
				value = value.replace("\"", "\"\"");
				return "\"" + value + "\"";
			}
			return value;
		}).collect(Collectors.joining(","));
	}
}

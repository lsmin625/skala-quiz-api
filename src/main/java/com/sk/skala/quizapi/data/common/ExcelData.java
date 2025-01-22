package com.sk.skala.quizapi.data.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sk.skala.quizapi.tools.StringTool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ExcelData {
	String sheetname;
	List<Header> headers;
	List<List<Object>> rows;

	public <T> void setRows(List<T> data) throws Exception {
		this.rows = buildRows(headers, data);
	}

	public String getFilename() {
		return sheetname + "-" + System.currentTimeMillis() + ".xlsx";
	}

	@Data
	public static class Header {
		private String name;
		private String value;

		public Header() {
		}

		public Header(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public static <T> List<List<Object>> buildRows(List<Header> headers, List<T> data) {
		List<List<Object>> rows = new ArrayList<List<Object>>();
		for (Object datum : data) {
			List<Object> row = new ArrayList<Object>();
			for (Header header : headers) {
				Method getter;
				try {
					getter = datum.getClass().getMethod(StringTool.toCamel("get_" + header.getValue()));
					row.add(getter.invoke(datum));
				} catch (Exception e) {
					log.error("ExcelData.buildRows: {}", e.toString());
				}
			}
			rows.add(row);
		}
		return rows;
	}
}

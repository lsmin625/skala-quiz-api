package com.sk.skala.quizapi.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sk.skala.quizapi.config.Error;
import com.sk.skala.quizapi.data.common.ExcelData;
import com.sk.skala.quizapi.data.common.ExcelData.Header;
import com.sk.skala.quizapi.exception.ResponseException;

public class ExcelTool {
	private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public static ResponseEntity<ByteArrayResource> build(ExcelData excelData) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(excelData.getSheetname());
			List<Header> headers = excelData.getHeaders();
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.size(); i++) {
				headerRow.createCell(i).setCellValue(headers.get(i).getName());
			}

			int rowIndex = 1;
			List<List<Object>> rows = excelData.getRows();
			for (List<Object> columns : rows) {
				Row row = sheet.createRow(rowIndex++);
				for (int i = 0; i < columns.size(); i++) {
					if (columns.get(i) instanceof Integer) {
						row.createCell(i).setCellValue((Integer) columns.get(i));
					} else if (columns.get(i) instanceof Long) {
						row.createCell(i).setCellValue((Long) columns.get(i));
					} else if (columns.get(i) instanceof Float) {
						row.createCell(i).setCellValue((Float) columns.get(i));
					} else if (columns.get(i) instanceof Double) {
						row.createCell(i).setCellValue((Double) columns.get(i));
					} else if (columns.get(i) instanceof Short) {
						row.createCell(i).setCellValue((Short) columns.get(i));
					} else if (columns.get(i) instanceof Date) {
						row.createCell(i).setCellValue((Date) columns.get(i));
					} else {
						if (columns.get(i) != null) {
							row.createCell(i).setCellValue(columns.get(i).toString());
						} else {
							row.createCell(i).setCellValue("");
						}
					}
				}
			}

			workbook.write(out);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelData.getFilename())
					.body(new ByteArrayResource(out.toByteArray()));
		}
	}

	public static <T> List<T> parse(MultipartFile file, List<Header> headers, Class<T> datum) throws Exception {
		List<T> list = new ArrayList<T>();

		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0); // the first sheet
			Iterator<Row> rows = sheet.iterator();
			while (rows.hasNext()) {
				Row currentRow = rows.next();
				if (currentRow.getRowNum() == 0) { // skip header row
					continue;
				}
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 0; i < headers.size(); i++) {
					row.put(headers.get(i).getValue(), getCellValue(currentRow.getCell(i)));
				}
				list.add(JsonTool.toObject(JsonTool.toString(row), datum));
			}
		} catch (IOException e) {
			throw new ResponseException(Error.INVALID_EXCEL_FORMAT);
		}
		return list;
	}

	private static Object getCellValue(Cell cell) {
		if (cell == null)
			return "";

		switch (cell.getCellType()) {
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		default:
			return "";
		}
	}
}

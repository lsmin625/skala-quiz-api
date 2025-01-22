package com.sk.skala.quizapi.data.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.sk.skala.quizapi.data.table.TalHandover.NbTac;
import com.sk.skala.quizapi.tools.StringTool;

import lombok.Data;

@Data
public class TalP1005010128Csv {
	public static final String[] FIELDS = { "talId", "attTotCnt", "intraAttTot", "nbTac1", "nbTacAttTot1", "nbTac2",
			"nbTacAttTot2", "nbTac3", "nbTacAttTot3", "nbTac4", "nbTacAttTot4", "nbTac5", "nbTacAttTot5", "nbTac6",
			"nbTacAttTot6", "nbTac7", "nbTacAttTot7", "nbTac8", "nbTacAttTot8", "nbTac9", "nbTacAttTot9", "nbTac10",
			"nbTacAttTot10", "nbTac11", "nbTacAttTot11", "nbTac12", "nbTacAttTot12", "nbTac13", "nbTacAttTot13",
			"nbTac14", "nbTacAttTot14", "nbTac15", "nbTacAttTot15" };

	private final Map<String, String> fieldsMap = new HashMap<>();

	public TalP1005010128Csv() {
		for (String field : FIELDS) {
			fieldsMap.put(field, null);
		}
	}

	public String getField(String fieldName) {
		return fieldsMap.get(fieldName);
	}

	public Long getFieldLong(String fieldName) {
		String value = fieldsMap.get(fieldName);
		return value == null ? 0 : Long.parseLong(value);
	}

	public void setField(String fieldName, String value) {
		fieldsMap.put(fieldName, value);
	}

	public List<NbTac> getNbTacList() {
		List<NbTac> list = new ArrayList<>();

		for (int i = 1; i <= 16; i++) {
			NbTac item = new NbTac();
			item.setNbTac(getField("nbTac" + i));
			item.setNbTacAttTot(getFieldLong("nbTacAttTot" + i));
			if (!StringTool.isEmpty(item.getNbTac())) {
				list.add(item);
			}
		}
		return list;
	}

	public static class CsvFieldSetMapper implements FieldSetMapper<TalP1005010128Csv> {
		@Override
		public TalP1005010128Csv mapFieldSet(FieldSet fieldSet) {
			TalP1005010128Csv csv = new TalP1005010128Csv();
			for (String field : TalP1005010128Csv.FIELDS) {
				csv.setField(field, fieldSet.readString(field));
			}
			return csv;
		}
	}
}

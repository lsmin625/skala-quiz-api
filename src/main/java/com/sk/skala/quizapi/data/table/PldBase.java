package com.sk.skala.quizapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sk.skala.quizapi.tools.JsonTool;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "son_pld_base")
public class PldBase extends Auditable<String> {

	@Id
	private Long id;

	private String pldName;

	@JsonIgnore
	private String pldParas;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PldPara {
		String paraName;
		String paraValue;
	}

	public List<PldPara> getPldParaList() {
		if (pldParas != null) {
			return JsonTool.toList(pldParas, PldPara.class);
		} else {
			return new ArrayList<PldPara>();
		}
	}

	public void setPldParaList(List<PldPara> list) {
		pldParas = JsonTool.toString(list);
	}
}

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
@Table(name = "son_pld_group")
public class PldGroup extends Auditable<String> {
	@Id
	private Long id;

	private String regionCode;
	private String serviceType;
	private String groupName;

	@JsonIgnore
	private String plds;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PldInfo {
		String pldName;
		String pldParas;
	}

	public List<PldInfo> getPldList() {
		if (plds != null) {
			return JsonTool.toList(plds, PldInfo.class);
		} else {
			return new ArrayList<PldInfo>();
		}
	}

	public void setPldList(List<PldInfo> list) {
		plds = JsonTool.toString(list);
	}
}

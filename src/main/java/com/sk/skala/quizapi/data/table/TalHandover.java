package com.sk.skala.quizapi.data.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sk.skala.quizapi.tools.JsonTool;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "son_btal_ho")
public class TalHandover {

	@EmbeddedId
	private TalHandoverId id;

	private Long attTotCnt;
	private Long intraAttTot;

	@JsonIgnore
	private String nbTacs;

	public TalHandover() {
	}

	public TalHandover(TalHandoverId id, Long attTotCnt, Long intraAttTot, String nbTacs) {
		this.id = id;
		this.attTotCnt = attTotCnt;
		this.intraAttTot = intraAttTot;
		this.nbTacs = nbTacs;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class NbTac {
		String nbTac;
		Long nbTacAttTot;
	}

	public List<NbTac> getNbTacList() {
		if (nbTacs != null) {
			return JsonTool.toList(nbTacs, NbTac.class);
		} else {
			return new ArrayList<NbTac>();
		}
	}

	public void setNbTacList(List<NbTac> list) {
		nbTacs = JsonTool.toString(list);
	}
}

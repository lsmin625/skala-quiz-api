package com.sk.skala.quizapi.data.table;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TalHandoverId {
	private Date collectedDate;
	private String fileName;
	private Long talId;

	public TalHandoverId() {
	}

	public TalHandoverId(Date collectedDate, String fileName, Long talId) {
		this.collectedDate = collectedDate;
		this.fileName = fileName;
		this.talId = talId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TalHandoverId that = (TalHandoverId) o;
		return collectedDate.equals(that.collectedDate) && fileName.equals(that.fileName) && talId.equals(that.talId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collectedDate, fileName, talId);
	}
}

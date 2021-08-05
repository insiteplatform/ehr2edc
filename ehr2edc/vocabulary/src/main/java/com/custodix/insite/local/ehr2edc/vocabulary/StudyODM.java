package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class StudyODM {
	@NotBlank
	private final String odm;

	private StudyODM(String odm) {
		this.odm = odm;
	}

	public static StudyODM of(String odm) {
		return new StudyODM(odm);
	}

	public String getOdm() {
		return odm;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final StudyODM studyODM = (StudyODM) o;
		return Objects.equals(odm, studyODM.odm);
	}

	@Override
	public int hashCode() {
		return Objects.hash(odm);
	}
}

package com.custodix.insite.local.ehr2edc.query.executor.common.concept;

import java.io.Serializable;
import java.util.Objects;

public final class ConceptCode implements Serializable {
	private String code;

	private ConceptCode(final String code) {
		this.code = code;
	}

	public static ConceptCode conceptFor(String code) {
		return new ConceptCode(code);
	}

	public String getCode() {
		return code;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ConceptCode that = (ConceptCode) o;
		return Objects.equals(code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public String toString() {
		return "observationconcept=" + code;
	}
}

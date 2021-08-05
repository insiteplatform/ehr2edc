package com.custodix.insite.local.ehr2edc.query.fhir.vocabulary;

import java.util.Objects;

public final class FhirResourceId {
	private final String id;

	private FhirResourceId(String id) {
		this.id = id;
	}

	public static FhirResourceId of(String id) {
		return new FhirResourceId(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FhirResourceId that = (FhirResourceId) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "FhirResourceId{" + "id='" + id + '\'' + '}';
	}
}

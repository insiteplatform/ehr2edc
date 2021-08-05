package com.custodix.insite.local.ehr2edc.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

public final class PopulationContextId {
	@NotBlank
	private final String id;

	private PopulationContextId(String id) {
		this.id = id;
	}

	public static PopulationContextId of(String value) {
		return new PopulationContextId(value);
	}

	public String getId() {
		return id;
	}

	public static PopulationContextId generate() {
		final UUID uuid = UUID.randomUUID();
		return PopulationContextId.of(abs(uuid.getMostSignificantBits()) + "");
	}

	@Override
	public String toString() {
		return "PopulationContextId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final PopulationContextId populationContextId = (PopulationContextId) o;
		return id.equals(populationContextId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

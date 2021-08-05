package eu.ehr4cr.workbench.local.vocabulary.cohort.cohortbuilder;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonValue;

public class FilterIdentifier {
	public static final FilterIdentifier NULL = new FilterIdentifier(0);
	private final int filterId;

	public FilterIdentifier(int filterId) {
		this.filterId = filterId;
	}

	@JsonValue
	public int getFilterId() {
		return filterId;
	}

	public static FilterIdentifier generate() {
		final UUID uuid = UUID.randomUUID();
		final Long generatedId = abs(uuid.getMostSignificantBits());
		return new FilterIdentifier(abs(generatedId.intValue()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FilterIdentifier that = (FilterIdentifier) o;
		return filterId == that.filterId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(filterId);
	}
}

package com.custodix.insite.mongodb.export.patient.domain.model.demographic;

import java.time.*;
import java.util.Optional;

public class BirthInformation implements DemographicFact {

	private static final String DEMOGRAPHIC_TYPE = "BIRTH_DATE";

	private final Instant birthDate;
	private final Accuracy accuracy;

	private BirthInformation(Builder builder) {
		birthDate = builder.birthDate;
		accuracy = builder.accuracy;
	}

	public Instant getBirthDate() {
		return birthDate;
	}

	public Accuracy getAccuracy() {
		return accuracy;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String factType() {
		return DEMOGRAPHIC_TYPE;
	}

	@Override
	public String factValue() {
		return Optional.ofNullable(birthDate)
				.map(instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
				.map(LocalDateTime::toLocalDate)
				.map(LocalDate::toString)
				.orElse("");
	}

	public enum Accuracy {
		UNSPECIFIED('?'),
		BIRTH_DATE_UNKNOWN('L'),
		DAY('D'),
		MONTH('B'),
		YEAR('F'),
		HOUR('H'),
		MINUTE('I'),
		SECOND('C');

		private final Character code;

		Accuracy(Character code) {
			this.code = code;
		}

		public Character getCode() {
			return code;
		}

		@Override
		public String toString() {
			return this.name();
		}
	}

	public static final class Builder {
		private Instant birthDate;
		private Accuracy accuracy;

		private Builder() {
		}

		public Builder withBirthDate(Instant val) {
			birthDate = val;
			return this;
		}

		public Builder withAccuracy(Accuracy val) {
			accuracy = val;
			return this;
		}

		public BirthInformation build() {
			return new BirthInformation(this);
		}
	}
}

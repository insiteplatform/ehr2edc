package com.custodix.insite.mongodb.export.patient.domain.model.demographic;

import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus.Status.ALIVE;
import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus.Status.DECEASED;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class DeathInformation implements DemographicFact {

	private static final String DEMOGRAPHIC_TYPE = "DEATH_DATE";

	private final Instant deathDate;
	private final Accuracy accuracy;

	private DeathInformation(Builder builder) {
		deathDate = builder.deathDate;
		accuracy = builder.accuracy;
	}

	public Instant getDeathDate() {
		return deathDate;
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
		return Optional.ofNullable(deathDate)
				.map(instant -> LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
				.map(LocalDateTime::toLocalDate)
				.map(LocalDate::toString)
				.orElse("");
	}

	public enum Accuracy {
		UNSPECIFIED('?', VitalStatus.Status.UNKNOWN),
		PATIENT_ALIVE('N', ALIVE),
		UNKNOWN('Z', DECEASED),
		DECEASED_STATUS_UNKNOWN('U', VitalStatus.Status.UNKNOWN),
		DAY('Y', DECEASED),
		MONTH('M', DECEASED),
		YEAR('X', DECEASED),
		HOUR('R', DECEASED),
		MINUTE('T', DECEASED),
		SECOND('S', DECEASED);

		private final Character code;
		private final VitalStatus.Status status;

		Accuracy(Character code, VitalStatus.Status status) {
			this.code = code;
			this.status = status;
		}

		public Character getCode() {
			return code;
		}

		public VitalStatus.Status toVitalStatus() {
			return status;
		}

		@Override
		public String toString() {
			return this.name();
		}
	}

	public static final class Builder {
		private Instant deathDate;
		private Accuracy accuracy;

		private Builder() {
		}

		public Builder withDeathDate(Instant val) {
			deathDate = val;
			return this;
		}

		public Builder withAccuracy(Accuracy val) {
			accuracy = val;
			return this;
		}

		public DeathInformation build() {
			return new DeathInformation(this);
		}
	}
}
package eu.ehr4cr.workbench.local.model.patient;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class PatientFactGroup {
	private final LocalDate date;
	private final String header;
	private final List<Observation> observations;

	private PatientFactGroup(Builder builder) {
		date = builder.date;
		header = builder.header;
		observations = builder.observations;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@JsonIgnore
	public LocalDate getDate() {
		return date;
	}

	public String getHeader() {
		return header;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	public static final class Builder {
		private LocalDate date;
		private String header;
		private List<Observation> observations;

		private Builder() {
		}

		public Builder withDate(LocalDate date) {
			this.date = date;
			return this;
		}

		public Builder withHeader(String header) {
			this.header = header;
			return this;
		}

		public Builder withObservations(List<Observation> observations) {
			this.observations = observations;
			return this;
		}

		public PatientFactGroup build() {
			return new PatientFactGroup(this);
		}
	}
}

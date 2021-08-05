package eu.ehr4cr.workbench.local.model.patient;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public final class Fact {
	private final LocalDate startDate;
	private final String observationName;
	private final String observationAttribute;
	private final String observationValue;
	private final String sourceName;
	private final Double observationNumValue;

	private Fact(Builder builder) {
		startDate = builder.startDate;
		observationName = builder.observationName;
		observationAttribute = builder.observationAttribute;
		observationValue = builder.observationValue;
		sourceName = builder.sourceName;
		observationNumValue = builder.observationNumValue;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
	public LocalDate getStartDate() {
		return startDate;
	}

	public String getObservationName() {
		return observationName;
	}

	public String getObservationAttribute() {
		return observationAttribute;
	}

	public String getObservationValue() {
		return observationValue;
	}

	public String getSourceName() {
		return sourceName;
	}

	public Double getObservationNumValue() {
		return observationNumValue;
	}

	public static final class Builder {
		private LocalDate startDate;
		private String observationName;
		private String observationAttribute;
		private String observationValue;
		private String sourceName;
		private Double observationNumValue;

		private Builder() {
		}

		public Builder withStartDate(LocalDate startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withObservationName(String observationName) {
			this.observationName = observationName;
			return this;
		}

		public Builder withObservationAttribute(String observationAttribute) {
			this.observationAttribute = observationAttribute;
			return this;
		}

		public Builder withObservationValue(String observationValue) {
			this.observationValue = observationValue;
			return this;
		}

		public Builder withSourceName(String sourceName) {
			this.sourceName = sourceName;
			return this;
		}

		public Builder withObservationNumValue(Double observationNumValue) {
			this.observationNumValue = observationNumValue;
			return this;
		}

		public Fact build() {
			return new Fact(this);
		}
	}
}

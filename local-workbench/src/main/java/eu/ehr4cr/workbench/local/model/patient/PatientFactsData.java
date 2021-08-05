package eu.ehr4cr.workbench.local.model.patient;

import java.util.List;

public final class PatientFactsData {
	private final int patientId;
	private final int pageSize;
	private final long observationsSize;
	private final long factsSize;
	private final List<PatientFactGroup> factGroups;

	private PatientFactsData(Builder builder) {
		patientId = builder.patientId;
		pageSize = builder.pageSize;
		observationsSize = builder.observationsSize;
		factsSize = builder.factsSize;
		factGroups = builder.factGroups;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public int getPatientId() {
		return patientId;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getObservationsSize() {
		return observationsSize;
	}

	public long getFactsSize() {
		return factsSize;
	}

	public List<PatientFactGroup> getFactGroups() {
		return factGroups;
	}

	public static final class Builder {
		private int patientId;
		private int pageSize;
		private long observationsSize;
		private long factsSize;
		private List<PatientFactGroup> factGroups;

		private Builder() {
		}

		public Builder withPatientCDWReference(int patientId) {
			this.patientId = patientId;
			return this;
		}

		public Builder withPageSize(int pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		public Builder withObservationsSize(long observationsSize) {
			this.observationsSize = observationsSize;
			return this;
		}

		public Builder withFactsSize(long factsSize) {
			this.factsSize = factsSize;
			return this;
		}

		public Builder withFactGroups(List<PatientFactGroup> factGroups) {
			this.factGroups = factGroups;
			return this;
		}

		public PatientFactsData build() {
			return new PatientFactsData(this);
		}
	}
}

package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Optional;

public final class VitalSignConcept {
	private final ConceptCode concept;
	private final String component;
	private final String location;
	private final String laterality;
	private final String position;

	private VitalSignConcept(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		location = builder.location;
		laterality = builder.laterality;
		position = builder.position;
	}

	public static VitalSignConcept from(
			com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept vitalSignConcept) {
		ConceptCode concept = Optional.ofNullable(vitalSignConcept.getConcept())
				.map(ConceptCode::from)
				.orElse(null);
		return VitalSignConcept.newBuilder()
				.withConcept(concept)
				.withComponent(vitalSignConcept.getComponent())
				.withLocation(vitalSignConcept.getLocation())
				.withLaterality(vitalSignConcept.getLaterality())
				.withPosition(vitalSignConcept.getPosition())
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ConceptCode getConcept() {
		return concept;
	}

	public String getComponent() {
		return component;
	}

	public String getLocation() {
		return location;
	}

	public String getLaterality() {
		return laterality;
	}

	public String getPosition() {
		return position;
	}

    public static final class Builder {
		private ConceptCode concept;
		private String component;
		private String location;
		private String laterality;
		private String position;

		private Builder() {
		}

		public Builder withConcept(ConceptCode concept) {
			this.concept = concept;
			return this;
		}

		public Builder withComponent(String component) {
			this.component = component;
			return this;
		}

		public Builder withLocation(String location) {
			this.location = location;
			return this;
		}

		public Builder withLaterality(String laterality) {
			this.laterality = laterality;
			return this;
		}

		public Builder withPosition(String position) {
			this.position = position;
			return this;
		}

		public VitalSignConcept build() {
			return new VitalSignConcept(this);
		}
	}
}

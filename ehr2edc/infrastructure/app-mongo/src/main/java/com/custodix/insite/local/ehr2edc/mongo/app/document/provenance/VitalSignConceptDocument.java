package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.VitalSignConcept;

final class VitalSignConceptDocument {
	private final ConceptCodeDocument concept;
	private final String component;
	private final String location;
	private final String laterality;
	private final String position;

	@PersistenceConstructor
	private VitalSignConceptDocument(ConceptCodeDocument concept, String component, String location,
			String laterality, String position) {
		this.concept = concept;
		this.component = component;
		this.location = location;
		this.laterality = laterality;
		this.position = position;
	}

	private VitalSignConceptDocument(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		location = builder.location;
		laterality = builder.laterality;
		position = builder.position;
	}

	public static VitalSignConceptDocument toDocument(VitalSignConcept vitalSignConcept) {
		ConceptCodeDocument concept = Optional.ofNullable(vitalSignConcept.getConcept())
				.map(ConceptCodeDocument::toDocument)
				.orElse(null);
		return VitalSignConceptDocument.newBuilder()
				.withConcept(concept)
				.withComponent(vitalSignConcept.getComponent())
				.withLocation(vitalSignConcept.getLocation())
				.withLaterality(vitalSignConcept.getLaterality())
				.withPosition(vitalSignConcept.getPosition())
				.build();
	}

	public VitalSignConcept restore() {
		ConceptCode conceptCode = Optional.ofNullable(concept)
				.map(ConceptCodeDocument::restore)
				.orElse(null);
		return VitalSignConcept.newBuilder()
				.withConcept(conceptCode)
				.withComponent(component)
				.withLocation(location)
				.withLaterality(laterality)
				.withPosition(position)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ConceptCodeDocument concept;
		private String component;
		private String location;
		private String laterality;
		private String position;

		private Builder() {
		}

		public Builder withConcept(ConceptCodeDocument concept) {
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

		public VitalSignConceptDocument build() {
			return new VitalSignConceptDocument(this);
		}
	}
}

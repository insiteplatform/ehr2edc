package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Document(collection = VitalSignDocument.COLLECTION)
public final class VitalSignDocument {

	public static final String COLLECTION = "VitalSign";
	@Id
	@SuppressWarnings("unused")
	private String id;

	private final VitalSignConceptField concept;
	private final SubjectId subjectId;
	private final LocalDateTime effectiveDateTime;
	private final VitalSignMeasurementField measurement;

	@PersistenceConstructor
	private VitalSignDocument(VitalSignConceptField concept, SubjectId subjectId, LocalDateTime effectiveDateTime,
			VitalSignMeasurementField measurement) {
		this.concept = concept;
		this.subjectId = subjectId;
		this.effectiveDateTime = effectiveDateTime;
		this.measurement = measurement;
	}

	private VitalSignDocument(Builder builder) {
		concept = builder.concept;
		subjectId = builder.subjectId;
		effectiveDateTime = builder.effectiveDateTime;
		measurement = builder.measurement;
	}

	public String getId() {
		return id;
	}

	public VitalSignConceptField getConcept() {
		return concept;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDateTime getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public VitalSignMeasurementField getMeasurement() {
		return measurement;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.concept = concept;
		builder.subjectId = subjectId;
		builder.effectiveDateTime = effectiveDateTime;
		builder.measurement = measurement;
		return builder;
	}

	public static final class Builder {
		private VitalSignConceptField concept;
		private SubjectId subjectId;
		private LocalDateTime effectiveDateTime;
		private VitalSignMeasurementField measurement;

		private Builder() {
		}

		public Builder withConcept(VitalSignConceptField concept) {
			this.concept = concept;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withEffectiveDateTime(LocalDateTime effectiveDateTime) {
			this.effectiveDateTime = effectiveDateTime;
			return this;
		}

		public Builder withMeasurement(VitalSignMeasurementField measurement) {
			this.measurement = measurement;
			return this;
		}

		public VitalSignDocument build() {
			return new VitalSignDocument(this);
		}
	}
}

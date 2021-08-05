package com.custodix.insite.mongodb.vocabulary;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PatientIdentifier.Builder.class)
public class PatientIdentifier {

	@Valid
	@NotNull
	private final PatientId patientId;
	@Valid
	@NotNull
	private final Namespace namespace;
	@Valid
	@NotNull
	private final SubjectId subjectId;

	private PatientIdentifier(final Builder builder) {
		patientId = builder.patientId;
		namespace = builder.namespace;
		subjectId = builder.subjectId;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.patientId = getPatientId();
		builder.namespace = getNamespace();
		builder.subjectId = getSubjectId();
		return builder;
	}

	public PatientId getPatientId() {
		return patientId;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public static PatientIdentifier of(PatientId patientId, Namespace namespace, SubjectId subjectId) {
		return PatientIdentifier.newBuilder()
				.withPatientId(patientId)
				.withNamespace(namespace)
				.withSubjectId(subjectId)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientId patientId;
		private Namespace namespace;
		private SubjectId subjectId;

		private Builder() {
		}

		public Builder withPatientId(PatientId val) {
			patientId = val;
			return this;
		}

		public Builder withNamespace(Namespace val) {
			namespace = val;
			return this;
		}

		public Builder withSubjectId(SubjectId val) {
			subjectId = val;
			return this;
		}

		public PatientIdentifier build() {
			return new PatientIdentifier(this);
		}
	}
}
package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.export.patient.snapshot.ActiveSubjectSnapshot;
import com.custodix.insite.mongodb.export.patient.vocabulary.ActiveSubjectId;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = ActiveSubjectDocument.COLLECTION)
public final class ActiveSubjectDocument {
	public static final String COLLECTION = "ActiveSubject";

	@Id
	private String id;
	private final SubjectId subjectId;
	private final String patientId;
	private final String patientIdSource;

	@PersistenceConstructor
	private ActiveSubjectDocument(final String id, final SubjectId subjectId, final String patientId, final String patientIdSource) {
		this.id = id;
		this.subjectId = subjectId;
		this.patientId = patientId;
		this.patientIdSource = patientIdSource;
	}

	private ActiveSubjectDocument(final Builder builder) {
		id = builder.id;
		subjectId = builder.subjectId;
		patientId = builder.patientId;
		patientIdSource = builder.patientIdSource;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getPatientIdSource() {
		return patientIdSource;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static ActiveSubjectDocument fromSnapshot(final ActiveSubjectSnapshot activeSubjectSnapshot) {
		return ActiveSubjectDocument.newBuilder()
				.withId(activeSubjectSnapshot.getId().getId())
				.withPatientId(activeSubjectSnapshot.getPatientIdentifier().getPatientId().getId())
				.withPatientIdSource(activeSubjectSnapshot.getPatientIdentifier().getNamespace().getName())
				.withSubjectId(activeSubjectSnapshot.getPatientIdentifier().getSubjectId())
				.build();
	}

	public ActiveSubjectSnapshot toSnapshot() {
		return ActiveSubjectSnapshot.newBuilder()
				.withId(ActiveSubjectId.of(id))
				.withPatientIdentifier(getPatientIdentifier())
				.build();
	}

	private PatientIdentifier getPatientIdentifier() {
		return PatientIdentifier.of(PatientId.of(patientId), Namespace.of(patientIdSource), subjectId);
	}

	public static final class Builder {
		private String id;
		private SubjectId subjectId;
		private String patientId;
		private String patientIdSource;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withPatientId(final String val) {
			patientId = val;
			return this;
		}

		public Builder withPatientIdSource(final String val) {
			patientIdSource = val;
			return this;
		}

		public ActiveSubjectDocument build() {
			return new ActiveSubjectDocument(this);
		}
	}
}

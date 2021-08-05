package com.custodix.insite.mongodb.export.patient.domain.model;

import com.custodix.insite.mongodb.export.patient.snapshot.ActiveSubjectSnapshot;
import com.custodix.insite.mongodb.export.patient.vocabulary.ActiveSubjectId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public final class ActiveSubject {

	private final ActiveSubjectId id;
	private final PatientIdentifier patientIdentifier;

	private ActiveSubject(final ActiveSubjectSnapshot activeSubjectSnapshot) {
		id = activeSubjectSnapshot.getId();
		patientIdentifier = activeSubjectSnapshot.getPatientIdentifier();
	}

	private ActiveSubject(final ActiveSubjectId id, final PatientIdentifier patientIdentifier) {
		this.id = id;
		this.patientIdentifier = patientIdentifier;
	}

	public static ActiveSubject instanceFor(final PatientIdentifier patientIdentifier) {
		return new ActiveSubject(ActiveSubjectId.generate(), patientIdentifier);
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public ActiveSubjectSnapshot toSnapshot() {
		return ActiveSubjectSnapshot.newBuilder()
				.withId(id)
				.withPatientIdentifier(patientIdentifier)
				.build();
	}

	public static ActiveSubject restoreFrom(ActiveSubjectSnapshot activeSubjectSnapshot) {
		return new ActiveSubject(activeSubjectSnapshot);
	}


}

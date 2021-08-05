package com.custodix.insite.local.ehr2edc.snapshots;

import static java.util.UUID.randomUUID;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectSnapshotObjectMother {

	public static SubjectSnapshot aDefaultSubjectSnapshot() {
		return aDefaultSubjectSnapshotBuilder().build();
	}

	public static SubjectSnapshot.Builder aDefaultSubjectSnapshotBuilder() {
		String subjectId = randomUUID().toString();
		return SubjectSnapshot.newBuilder()
				.withPatientCDWReference(PatientCDWReference.newBuilder()
						.withId(randomUUID().toString())
						.withSource("MASTER_PATIENT_INDEX")
						.build())
				.withSubjectId(SubjectId.of(subjectId))
				.withEdcSubjectReference(EDCSubjectReference.of(randomUUID().toString()));

	}
}
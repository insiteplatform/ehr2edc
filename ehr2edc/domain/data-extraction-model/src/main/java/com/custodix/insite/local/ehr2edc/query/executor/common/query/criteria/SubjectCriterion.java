package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class SubjectCriterion implements Criterion {
	private final SubjectId subjectId;
	private final PatientCDWReference patientCDWReference;

	private SubjectCriterion(SubjectId subjectId, PatientCDWReference patientCDWReference) {
		this.subjectId = subjectId;
		this.patientCDWReference = patientCDWReference;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	public static SubjectCriterion subjectIs(SubjectId subjectId, PatientCDWReference patientCDWReference) {
		return new SubjectCriterion(subjectId, patientCDWReference);
	}

	public static SubjectCriterion subjectIs(SubjectId subjectId) {
		return new SubjectCriterion(subjectId, null);
	}

	public static SubjectCriterion subjectIs(PatientCDWReference patientCDWReference) {
		return new SubjectCriterion(null, patientCDWReference);
	}

	public static SubjectCriterion subjectIs(String subjectId) {
		return subjectIs(SubjectId.of(subjectId));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SubjectCriterion that = (SubjectCriterion) o;
		return Objects.equals(subjectId, that.subjectId) && Objects.equals(patientCDWReference,
				that.patientCDWReference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subjectId, patientCDWReference);
	}
}

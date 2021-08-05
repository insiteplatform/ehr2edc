package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SubjectCriterionMixin {
	@JsonProperty
	private SubjectId subjectId;
	@JsonProperty
	private PatientCDWReference patientCDWReference;

	@JsonCreator
	private SubjectCriterionMixin(@JsonProperty("subjectId") final SubjectId subjectId,
			@JsonProperty("patientCDWReference") final PatientCDWReference patientCDWReference) {
	}
}

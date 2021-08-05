package com.custodix.insite.local.ehr2edc.query;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface SubjectProjection {

	SubjectId getSubjectId();

	EDCSubjectReference getEdcSubjectReference();

	LocalDate getDateOfConsent();

	boolean isActive();

	PatientCDWReference getPatientCDWReference();

	LocalDate getDateOfConsentWithdrawn();

	DeregisterReason getDeregisterReason();
}

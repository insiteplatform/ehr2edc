package com.custodix.insite.local.ehr2edc.domain.service;

import java.util.List;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.patient.PatientDomain;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface PatientEHRGateway {

	boolean exists(StudyId studyId, PatientSearchCriteria patientSearchCriteria);

	Set<PatientCDWReference> getFiltered(StudyId studyId, String patientDomain, String filter, int limit);

	List<PatientDomain> getPatientDomains(StudyId studyId);

}

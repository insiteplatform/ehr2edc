package com.custodix.insite.local.ehr2edc.query.patient;

import java.util.List;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.patient.PatientDomain;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;

public interface PatientEHRGatewayStrategy {

	boolean exists(PatientSearchCriteria patientSearchCriteria);

	Set<PatientCDWReference> getFiltered(String patientDomain, String filter, int limit);

	List<PatientDomain> getPatientDomains();
}

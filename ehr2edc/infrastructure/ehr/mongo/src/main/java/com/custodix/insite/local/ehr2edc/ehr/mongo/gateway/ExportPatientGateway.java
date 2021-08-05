package com.custodix.insite.local.ehr2edc.ehr.mongo.gateway;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface ExportPatientGateway {
	void migrate(StudyId studyId, SubjectId subjectId, PatientCDWReference patientCDWReference);
}

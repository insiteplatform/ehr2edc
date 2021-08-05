package com.custodix.insite.local.ehr2edc.ehr.mongo.gateway;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class ExportPatientGatewayImpl implements ExportPatientGateway {

	private final ExportPatient exportPatient;

	public ExportPatientGatewayImpl(ExportPatient exportPatient) {
		this.exportPatient = exportPatient;
	}

	@Override
	public void migrate(StudyId studyId, SubjectId subjectId, PatientCDWReference patientCDWReference) {
		PatientIdentifier patientIdentifier = toPatientIdentifier(subjectId, patientCDWReference);
		ExportPatient.Request request = ExportPatient.Request.newBuilder()
				.withPatientIdentifier(patientIdentifier)
				.build();
		exportPatient.export(request);
	}

	private PatientIdentifier toPatientIdentifier(SubjectId subjectId, PatientCDWReference patientCDWReference) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(patientCDWReference.getId()))
				.withNamespace(Namespace.of(patientCDWReference.getSource()))
				.withSubjectId(com.custodix.insite.mongodb.vocabulary.SubjectId.of(subjectId.getId()))
				.build();
	}
}

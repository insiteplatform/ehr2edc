package com.custodix.insite.local.ehr2edc.ehr.mongo.usecase;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.gateway.ExportPatientGateway;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Command
public class SubjectMigrationCommand implements SubjectMigration {
	private final ExportPatientGateway exportPatientGateway;
	private final EHRConnectionRepository ehrConnectionRepository;

	public SubjectMigrationCommand(ExportPatientGateway exportPatientGateway,
			EHRConnectionRepository ehrConnectionRepository) {
		this.exportPatientGateway = exportPatientGateway;
		this.ehrConnectionRepository = ehrConnectionRepository;
	}

	@Override
	public void migrate(Request request) {
		if (isConnectedToMongo(request.getStudyId())) {
			exportPatientGateway.migrate(request.getStudyId(), request.getSubjectId(),
					request.getPatientCDWReference());
		}
	}

	private boolean isConnectedToMongo(StudyId studyId) {
		return !ehrConnectionRepository.findByStudyId(studyId)
				.isPresent();
	}
}

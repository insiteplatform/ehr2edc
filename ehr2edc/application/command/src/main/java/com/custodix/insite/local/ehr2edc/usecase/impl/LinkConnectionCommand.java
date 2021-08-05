package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.LinkConnection;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Command
class LinkConnectionCommand implements LinkConnection {

	private final StudyRepository studyRepository;
	private final StudyConnectionRepository studyConnectionRepository;

	LinkConnectionCommand(StudyRepository studyRepository, StudyConnectionRepository studyConnectionRepository) {
		this.studyRepository = studyRepository;
		this.studyConnectionRepository = studyConnectionRepository;
	}

	@Override
	public void link(Request request) {
		assertStudyExists(request.getStudyId());
		studyConnectionRepository.save(toEDCConnection(request));
	}

	private void assertStudyExists(StudyId id) {
		studyRepository.getStudyById(id);
	}

	private ExternalEDCConnection toEDCConnection(Request request) {
		return ExternalEDCConnection.newBuilder()
				.withStudyId(request.getStudyId())
				.withConnectionType(request.getType())
				.withEdcSystem(request.getEdcSystem())
				.withExternalSiteId(request.getExternalSiteId())
				.withClinicalDataURI(request.getClinicalDataURI())
				.withUsername(request.getUsername())
				.withPassword(request.getPassword())
				.withEnabled(request.isEnabled())
				.withStudyIdOVerride(request.getStudyIdOverride())
				.build();
	}

}

package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyMetaData;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.UpdateStudyMetadata;
import com.custodix.insite.local.ehr2edc.domain.service.ODMParser;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class UpdateStudyMetadataCommand implements UpdateStudyMetadata {

	private final ODMParser odmParser;
	private final StudyRepository studyRepository;

	UpdateStudyMetadataCommand(ODMParser odmParser, StudyRepository studyRepository) {
		this.odmParser = odmParser;
		this.studyRepository = studyRepository;
	}

	@Override
	public void update(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		StudyMetaData studyMetaData = odmParser.parse(request.getStudyODM());
		study.updateFrom(studyMetaData);
		studyRepository.save(study);
	}
}

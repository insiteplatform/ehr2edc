package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyMetaData;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.CreateStudy;
import com.custodix.insite.local.ehr2edc.domain.service.ODMParser;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class CreateStudyCommand implements CreateStudy {

	private final ODMParser odmParser;
	private final StudyRepository studyRepository;

	CreateStudyCommand(ODMParser odmParser, StudyRepository studyRepository) {
		this.odmParser = odmParser;
		this.studyRepository = studyRepository;
	}

	@Override
	public Response create(Request request) {
		StudyMetaData studyMetaData = odmParser.parse(request.getStudyODM());
		Study study = Study.create(studyMetaData, studyRepository);
		studyRepository.save(study);
		return Response.newBuilder()
				.withStudyId(study.getStudyId())
				.build();
	}
}

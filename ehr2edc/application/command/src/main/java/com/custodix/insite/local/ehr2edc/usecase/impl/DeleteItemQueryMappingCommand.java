package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.DeleteItemQueryMapping;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class DeleteItemQueryMappingCommand implements DeleteItemQueryMapping {

	private final StudyRepository studyRepository;

	public DeleteItemQueryMappingCommand(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@Override
	public void delete(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		study.removeItemQueryMapping(request.getItemId());
		studyRepository.save(study);
	}
}

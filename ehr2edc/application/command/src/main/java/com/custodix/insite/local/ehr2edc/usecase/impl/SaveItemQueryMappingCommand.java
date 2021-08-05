package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.SaveItemQueryMapping;
import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;

@Command
class SaveItemQueryMappingCommand implements SaveItemQueryMapping {
	private final StudyRepository studyRepository;
	private final ItemQueryMappingService itemQueryMappingService;

	SaveItemQueryMappingCommand(final StudyRepository studyRepository,
			final ItemQueryMappingService itemQueryMappingService) {
		this.studyRepository = studyRepository;
		this.itemQueryMappingService = itemQueryMappingService;
	}

	@Override
	public void save(final Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		ItemQueryMappingJson itemQueryMappingJson = itemQueryMappingService.toItemQueryMappingJson(request.getQuery(),
				request.getProjectors());
		study.addItemQueryMapping(request.getItemId(), itemQueryMappingJson);

		studyRepository.save(study);
	}
}

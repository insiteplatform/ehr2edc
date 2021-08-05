package eu.ehr4cr.workbench.local.usecases.model.dto;

interface AbstractClinicalStudyWithActiveFilterDto {
	ClinicalStudyDto getStudy();

	ProtocolVersionDto getVersion();

	ProtocolVersionFilterDto getActiveFilter();

	default Long getStudyId() {
		return getStudy().getId();
	}

	default Long getVersionId() {
		return getVersion().getId();
	}

	default Long getActiveFilterId() {
		return getActiveFilter().getId();
	}

}
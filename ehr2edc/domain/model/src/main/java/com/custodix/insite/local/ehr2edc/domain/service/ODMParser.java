package com.custodix.insite.local.ehr2edc.domain.service;

import javax.validation.Valid;

import com.custodix.insite.local.ehr2edc.StudyMetaData;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

public interface ODMParser {

	@Valid
	StudyMetaData parse(StudyODM odm);

}

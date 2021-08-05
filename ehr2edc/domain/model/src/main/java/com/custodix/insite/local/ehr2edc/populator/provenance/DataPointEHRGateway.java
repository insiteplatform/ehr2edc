package com.custodix.insite.local.ehr2edc.populator.provenance;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface DataPointEHRGateway {

	List<String> findAllForSubject(SubjectId subjectId);

}

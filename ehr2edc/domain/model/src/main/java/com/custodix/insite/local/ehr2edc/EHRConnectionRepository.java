package com.custodix.insite.local.ehr2edc;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface EHRConnectionRepository {
	EHRConnection getByStudyId(StudyId studyId);

	Optional<EHRConnection> findByStudyId(StudyId studyId);

	Optional<EHRConnection> findByStudyIdAndSystem(StudyId studyId, EHRSystem system);

	void save(EHRConnection ehrConnection);
}

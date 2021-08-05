package com.custodix.insite.local.ehr2edc;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface StudyConnectionRepository {

	ExternalEDCConnection getStudyConnectionByIdAndType(StudyId studyId, StudyConnectionType type);

	ExternalEDCConnection getActive(StudyId studyId, StudyConnectionType type);

	ExternalEDCConnection getReadSubjectsStudyConnectionById(StudyId studyId);

	ExternalEDCConnection getWriteSubjectStudyConnectionById(StudyId studyId);

	ExternalEDCConnection getSubmitEventStudyConnectionById(StudyId studyId);

	ExternalEDCConnection getReadLabnamesStudyConnectionById(StudyId studyId);

	Optional<ExternalEDCConnection> findStudyConnectionByIdAndType(StudyId studyId, StudyConnectionType type);

	Optional<ExternalEDCConnection> findActive(StudyId studyId, StudyConnectionType type);

	void save(ExternalEDCConnection studyConnection);

	boolean isEnabled(StudyId studyId, StudyConnectionType type);
}

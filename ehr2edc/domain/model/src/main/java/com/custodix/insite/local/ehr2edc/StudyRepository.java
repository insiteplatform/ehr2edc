package com.custodix.insite.local.ehr2edc;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface StudyRepository {

	List<Study> findAll();

	Study getStudyById(StudyId studyId);

	Study findStudyByIdAndInvestigator(StudyId studyId, UserIdentifier investigatorId);

	Study getBySubjectId(SubjectId subjectId);

	List<Study> findAvailableStudiesForPatient(PatientCDWReference patientCDWReference, UserIdentifier investigatorId);

	List<Study> findRegisteredStudiesForPatient(PatientCDWReference patientCDWReference, UserIdentifier investigatorId);

	Subject findSubjectByIdAndStudyIdAndInvestigator(SubjectId subjectId, StudyId studyId,
			UserIdentifier userIdentifier);

	void save(Study study);

	boolean exists(StudyId studyId);

	void delete(Study study);

	Optional<Study> findBySubjectId(SubjectId subjectId);
}

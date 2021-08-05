package com.custodix.insite.mongodb.export.patient.domain.repository;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.ActiveSubject;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface ActiveSubjectEHRGateway {

	Optional<ActiveSubject> findBySubjectId(SubjectId subjectId);

	void save(ActiveSubject activeSubject);

	void delete(SubjectId subjectId);

	List<ActiveSubject> getAll();
}

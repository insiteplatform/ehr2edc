package com.custodix.insite.mongodb.export.patient.domain.repository;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface SubjectMigrationRepository {

	Optional<SubjectMigration> findBySubjectId(SubjectId subjectId);

	void save(SubjectMigration subjectMigration);

	SubjectMigration getBySubjectId(SubjectId subjectId);
}

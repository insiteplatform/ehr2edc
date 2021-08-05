package com.custodix.insite.mongodb.export.patient.infrastructure.subjectmigration;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class SubjectMigrationMongoRepository implements SubjectMigrationRepository {

	private final SubjectMigrationDocumentRepository subjectMigrationDocumentRepository;

	public SubjectMigrationMongoRepository(final SubjectMigrationDocumentRepository subjectMigrationDocumentRepository) {
		this.subjectMigrationDocumentRepository = subjectMigrationDocumentRepository;
	}

	@Override
	public Optional<SubjectMigration> findBySubjectId(final SubjectId subjectId) {
		return subjectMigrationDocumentRepository
				.findBySubjectId(subjectId)
				.map(subjectMigrationDocument -> SubjectMigration.restoreFrom(subjectMigrationDocument.toSnapshot()));
	}

	@Override
	public void save(final SubjectMigration subjectMigration) {
		subjectMigrationDocumentRepository.save(SubjectMigrationDocument.fromSnapshot(subjectMigration.toSnapshot()));
	}

	@Override
	public SubjectMigration getBySubjectId(final SubjectId subjectId) {
		return SubjectMigration.restoreFrom(subjectMigrationDocumentRepository.findBySubjectId(subjectId)
				.map(SubjectMigrationDocument::toSnapshot)
				.orElseThrow(() -> new DomainException("Cannot find SubjectMigration for subject id " + subjectId.getId())));
	}
}

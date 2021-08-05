package com.custodix.insite.mongodb.export.patient.infrastructure.activesubject;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.ActiveSubject;
import com.custodix.insite.mongodb.export.patient.domain.repository.ActiveSubjectEHRGateway;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.ActiveSubjectDocumentRepository;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class ActiveSubjectMongoGateway implements ActiveSubjectEHRGateway {

	private final ActiveSubjectDocumentRepository activeSubjectDocumentRepository;

	public ActiveSubjectMongoGateway(final ActiveSubjectDocumentRepository activeSubjectDocumentRepository) {
		this.activeSubjectDocumentRepository = activeSubjectDocumentRepository;
	}

	@Override
	public Optional<ActiveSubject> findBySubjectId(final SubjectId subjectId) {
		return activeSubjectDocumentRepository
				.findBySubjectId(subjectId)
				.map(subjectMigrationDocument -> ActiveSubject.restoreFrom(subjectMigrationDocument.toSnapshot()));
	}

	@Override
	public void save(final ActiveSubject activeSubject) {
		activeSubjectDocumentRepository.save(ActiveSubjectDocument.fromSnapshot(activeSubject.toSnapshot()));
	}

	@Override
	public void delete(final SubjectId subjectId) {
		activeSubjectDocumentRepository.deleteBySubjectId(subjectId);
	}

	@Override
	public List<ActiveSubject> getAll() {
		return activeSubjectDocumentRepository.findAll()
				.stream()
				.map(subjectMigrationDocument -> ActiveSubject.restoreFrom(subjectMigrationDocument.toSnapshot()))
				.collect(toList());
	}

}

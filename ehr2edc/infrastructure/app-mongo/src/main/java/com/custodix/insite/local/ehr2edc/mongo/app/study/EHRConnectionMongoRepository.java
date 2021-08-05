package com.custodix.insite.local.ehr2edc.mongo.app.study;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EHRConnectionDocument;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Repository
class EHRConnectionMongoRepository implements EHRConnectionRepository {
	private final EHRConnectionDocumentRepository ehrConnectionDocumentRepository;

	EHRConnectionMongoRepository(EHRConnectionDocumentRepository ehrConnectionDocumentRepository) {
		this.ehrConnectionDocumentRepository = ehrConnectionDocumentRepository;
	}

	@Override
	public EHRConnection getByStudyId(StudyId studyId) {
		return findByStudyId(studyId).orElseThrow(
				() -> DomainException.of("study.ehr.connection.not.found", studyId.getId()));
	}

	@Override
	public Optional<EHRConnection> findByStudyId(StudyId studyId) {
		return ehrConnectionDocumentRepository.findById(studyId.getId())
				.map(EHRConnectionDocument::toEHRConnection);
	}

	@Override
	public Optional<EHRConnection> findByStudyIdAndSystem(StudyId studyId, EHRSystem system) {
		return ehrConnectionDocumentRepository.findByStudyIdAndSystem(studyId.getId(), system)
				.map(EHRConnectionDocument::toEHRConnection);
	}

	@Override
	public void save(EHRConnection ehrConnection) {
		EHRConnectionDocument document = EHRConnectionDocument.from(ehrConnection);
		ehrConnectionDocumentRepository.save(document);
	}

	@Repository
	interface EHRConnectionDocumentRepository extends MongoRepository<EHRConnectionDocument, String> {
		Optional<EHRConnectionDocument> findByStudyIdAndSystem(String studyId, EHRSystem system);
	}
}

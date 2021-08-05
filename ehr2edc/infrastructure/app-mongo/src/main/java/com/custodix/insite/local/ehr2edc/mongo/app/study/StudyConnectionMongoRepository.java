package com.custodix.insite.local.ehr2edc.mongo.app.study;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocumentId;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Repository
public class StudyConnectionMongoRepository implements StudyConnectionRepository {
	private final StudyConnectionMongoSnapshotRepository connectionSnapshotRepository;

	public StudyConnectionMongoRepository(StudyConnectionMongoSnapshotRepository studyConnectionSnapshotRepository) {
		this.connectionSnapshotRepository = studyConnectionSnapshotRepository;
	}

	@Override
	public ExternalEDCConnection getStudyConnectionByIdAndType(StudyId studyId, StudyConnectionType type) {
		return connectionSnapshotRepository.findById(EDCConnectionDocumentId.from(studyId, type))
				.map(EDCConnectionDocument::to)
				.orElseThrow(() -> DomainException.of("study.connection.unknown", studyId, type));
	}

	@Override
	public ExternalEDCConnection getActive(StudyId studyId, StudyConnectionType type) {
		ExternalEDCConnection connection = getStudyConnectionByIdAndType(studyId, type);
		if (connection.isEnabled()) {
			return connection;
		} else {
			throw DomainException.of("study.connection.disabled", studyId, type);
		}
	}

	@Override
	public ExternalEDCConnection getReadSubjectsStudyConnectionById(StudyId studyId) {
		return getStudyConnectionByIdAndType(studyId, StudyConnectionType.READ_SUBJECTS);
	}

	@Override
	public ExternalEDCConnection getWriteSubjectStudyConnectionById(StudyId studyId) {
		return getStudyConnectionByIdAndType(studyId, StudyConnectionType.WRITE_SUBJECT);
	}

	@Override
	public ExternalEDCConnection getSubmitEventStudyConnectionById(StudyId studyId) {
		return getStudyConnectionByIdAndType(studyId, StudyConnectionType.SUBMIT_EVENT);
	}

	@Override
	public ExternalEDCConnection getReadLabnamesStudyConnectionById(StudyId studyId) {
		return getStudyConnectionByIdAndType(studyId, StudyConnectionType.READ_LABNAMES);
	}

	@Override
	public Optional<ExternalEDCConnection> findStudyConnectionByIdAndType(StudyId studyId, StudyConnectionType type) {
		return connectionSnapshotRepository.findById(EDCConnectionDocumentId.from(studyId, type))
				.map(EDCConnectionDocument::to);
	}

	@Override
	public Optional<ExternalEDCConnection> findActive(StudyId studyId, StudyConnectionType type) {
		return findStudyConnectionByIdAndType(studyId, type).filter(ExternalEDCConnection::isEnabled);
	}

	@Override
	public boolean isEnabled(StudyId studyId, StudyConnectionType type) {
		return findStudyConnectionByIdAndType(studyId, type).map(ExternalEDCConnection::isEnabled)
				.orElse(false);
	}

	@Override
	public void save(ExternalEDCConnection connection) {
		EDCConnectionDocument studyConnectionSnapshot = EDCConnectionDocument.from(connection);
		connectionSnapshotRepository.save(studyConnectionSnapshot);
	}

	@Repository
	public interface StudyConnectionMongoSnapshotRepository
			extends MongoRepository<EDCConnectionDocument, EDCConnectionDocumentId> {
	}

}

package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Repository
public interface ActiveSubjectDocumentRepository extends MongoRepository<ActiveSubjectDocument, String> {

	Optional<ActiveSubjectDocument> findBySubjectId(SubjectId subjectId);

	void deleteBySubjectId(SubjectId subjectId);
}

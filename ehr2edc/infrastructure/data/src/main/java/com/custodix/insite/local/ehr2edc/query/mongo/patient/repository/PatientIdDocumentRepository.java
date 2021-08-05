package com.custodix.insite.local.ehr2edc.query.mongo.patient.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.mongo.patient.model.PatientIdDocument;

@Repository
public interface PatientIdDocumentRepository
		extends MongoRepository<PatientIdDocument, String>, PatientIdCustomRepository {
	//To be deleted once E2E-630 is implemented
	Optional<PatientIdDocument> findBySourceAndIdentifier(String source, String identifier);

	Optional<PatientIdDocument> findBySourceAndIdentifierAndBirthDate(String source, String identifier,
			LocalDate birthDate);

	List<PatientIdDocument> findAllBySourceAndIdentifierLike(String source, String identifier);

	List<PatientIdDocument> findAllBySource(String source);
}

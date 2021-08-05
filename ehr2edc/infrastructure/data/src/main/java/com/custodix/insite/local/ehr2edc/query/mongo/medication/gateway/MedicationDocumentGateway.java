package com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway;

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion.subjectIs;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument;
import com.custodix.insite.local.ehr2edc.query.mongo.medication.repository.CustomMedicationRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Component
class MedicationDocumentGateway implements EHRGateway<Medications, MedicationQuery>, DataPointEHRGateway {
	private final CustomMedicationRepository medicationRepository;
	private final MedicationMongoQueryFactory medicationMongoQueryFactory;
	private final MongoCriteriaFactory mongoCriteriaFactory;

	MedicationDocumentGateway(final CustomMedicationRepository medicationRepository,
			MongoCriteriaFactory mongoCriteriaFactory, MedicationMongoQueryFactory medicationMongoQueryFactory) {
		this.medicationRepository = medicationRepository;
		this.medicationMongoQueryFactory = medicationMongoQueryFactory;
		this.mongoCriteriaFactory = mongoCriteriaFactory;
	}

	@Override
	public List<String> findAllForSubject(SubjectId subjectId) {
		org.springframework.data.mongodb.core.query.Criteria criteria = mongoCriteriaFactory.createSubjectCriterion(
				subjectIs(subjectId));
		return medicationRepository.findAllDocuments(criteria)
				.stream()
				.map(Document::toJson)
				.collect(toList());
	}

	@Override
	public boolean canHandle(com.custodix.insite.local.ehr2edc.query.executor.common.query.Query<?> query) {
		return query instanceof MedicationQuery;
	}

	@Override
	public Medications execute(MedicationQuery query, LocalDate referenceDate) {
		Criteria criteria = query.getCriteria();
		List<MedicationDocument> documents = medicationRepository.findAll(
				medicationMongoQueryFactory.createQuery(criteria, referenceDate));
		return documents.stream()
				.map(new ToMedication())
				.collect(collectingAndThen(toList(), Medications::new));
	}
}

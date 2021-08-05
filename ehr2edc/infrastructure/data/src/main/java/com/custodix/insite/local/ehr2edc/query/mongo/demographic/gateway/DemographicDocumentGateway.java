package com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway;

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion.subjectIs;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument;
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Component
class DemographicDocumentGateway implements EHRGateway<Demographics, DemographicQuery>, DataPointEHRGateway {
	private final DemographicRepository demographicRepository;
	private final MongoCriteriaFactory mongoCriteriaFactory;
	private final DemographicMongoCriteriaFactory demographicMongoCriteriaFactory;

	DemographicDocumentGateway(DemographicRepository demographicRepository, MongoCriteriaFactory mongoCriteriaFactory,
			DemographicMongoCriteriaFactory demographicMongoCriteriaFactory) {
		this.demographicRepository = demographicRepository;
		this.mongoCriteriaFactory = mongoCriteriaFactory;
		this.demographicMongoCriteriaFactory = demographicMongoCriteriaFactory;
	}

	@Override
	public List<String> findAllForSubject(SubjectId subjectId) {
		org.springframework.data.mongodb.core.query.Criteria criteria = mongoCriteriaFactory.createSubjectCriterion(
				subjectIs(subjectId));
		return demographicRepository.findAllDocuments(criteria)
				.stream()
				.map(Document::toJson)
				.collect(toList());
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof DemographicQuery;
	}

	@Override
	public Demographics execute(DemographicQuery query, LocalDate referenceDate) {
		List<DemographicDocument> documents = demographicRepository.findAll(
				demographicMongoCriteriaFactory.createCriteria(query.getCriteria()));
		return map(documents);
	}

	private Demographics map(List<DemographicDocument> documents) {
		return documents.stream()
				.map(this::map)
				.collect(collectingAndThen(toList(), Demographics::new));
	}

	private Demographic map(DemographicDocument document) {
		return Demographic.newBuilder()
				.withSubjectId(document.getSubjectId())
				.withDemographicType(document.getDemographicType())
				.withValue(document.getValue())
				.build();
	}
}

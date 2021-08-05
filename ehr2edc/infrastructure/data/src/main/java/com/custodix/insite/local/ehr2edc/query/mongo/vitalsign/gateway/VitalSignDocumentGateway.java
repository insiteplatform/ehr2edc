package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway;

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion.subjectIs;
import static com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway.ToVitalSign.toVitalSign;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository.VitalSignRepository;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Component
class VitalSignDocumentGateway implements EHRGateway<VitalSigns, VitalSignQuery>, DataPointEHRGateway {

	private final VitalSignRepository vitalSignRepository;
	private final VitalSignMongoQueryFactory vitalSignMongoQueryFactory;
	private final MongoCriteriaFactory mongoCriteriaFactory;

	VitalSignDocumentGateway(final VitalSignRepository vitalSignRepository, MongoCriteriaFactory mongoCriteriaFactory,
			VitalSignMongoQueryFactory vitalSignMongoQueryFactory) {
		this.vitalSignRepository = vitalSignRepository;
		this.vitalSignMongoQueryFactory = vitalSignMongoQueryFactory;
		this.mongoCriteriaFactory = mongoCriteriaFactory;
	}

	@Override
	public List<String> findAllForSubject(SubjectId subjectId) {
		org.springframework.data.mongodb.core.query.Criteria criteria = mongoCriteriaFactory.createSubjectCriterion(
				subjectIs(subjectId));
		return vitalSignRepository.findAllDocuments(criteria)
				.stream()
				.map(Document::toJson)
				.collect(toList());
	}

	@Override
	public boolean canHandle(com.custodix.insite.local.ehr2edc.query.executor.common.query.Query<?> query) {
		return query instanceof VitalSignQuery;
	}

	@Override
	public VitalSigns execute(VitalSignQuery query, LocalDate referenceDate) {
		Criteria criteria = query.getCriteria();
		validCriteriaGuard(criteria);

		List<VitalSign> result = findVitalSigns(criteria, referenceDate);
		result.addAll(addMissingVitalSigns(criteria, result));

		return result.stream()
				.collect(collectingAndThen(toList(), VitalSigns::new));
	}

	private void validCriteriaGuard(Criteria criteria) {
		subjectCriterionGuard(criteria);
		filterCriterionGuard(criteria);
	}

	private void subjectCriterionGuard(Criteria criteria) {
		criteria.subject();
	}

	private void filterCriterionGuard(Criteria criteria) {
		if (criteria.queryFilters()
				.isEmpty()) {
			throw new DomainException("An additional criterion is missing");
		}
	}

	private List<VitalSign> findVitalSigns(Criteria criteria, LocalDate referenceDate) {
		if (hasCriteriaForKnownVitalSigns(criteria)) {
			Query query = vitalSignMongoQueryFactory.createQuery(criteria, referenceDate);
			return vitalSignRepository.findAll(query)
					.stream()
					.map(toVitalSign())
					.collect(toList());
		}
		return new ArrayList<>();
	}

	private boolean hasCriteriaForKnownVitalSigns(Criteria criteria) {
		return !criteria.queryFilters()
				.isEmpty();
	}

	private List<VitalSign> addMissingVitalSigns(Criteria criteria, List<VitalSign> result) {
		SubjectId subjectId = criteria.subject()
				.getSubjectId();
		return missingConceptsToInclude(criteria).filter(notPresentIn(result))
				.map(toEmptyVitalSignFor(subjectId))
				.collect(toList());
	}

	private Stream<ConceptCode> missingConceptsToInclude(Criteria criteria) {
		return criteria.concepts()
				.filter(ConceptCriterion::getIncludeMissing)
				.map(ConceptCriterion::getConcepts)
				.map(Collection::stream)
				.orElse(Stream.empty());
	}

	private Predicate<ConceptCode> notPresentIn(List<VitalSign> result) {
		return concept -> result.stream()
				.map(VitalSign::getConcept)
				.noneMatch(concept::equals);
	}

	private Function<ConceptCode, VitalSign> toEmptyVitalSignFor(SubjectId subject) {
		return conceptCode -> VitalSign.newBuilder()
				.withSubjectId(subject)
				.withConcept(VitalSignConcept.newBuilder()
						.withConcept(conceptCode)
						.build())
				.build();
	}
}

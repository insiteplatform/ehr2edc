package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoSortFactory;

@Component
public class LabMongoQueryFactory {
	private static final String FIELD_CONCEPT = "labConcept.concept";
	private static final String FIELD_START_DATE = "startDate";
	private static final String FIELD_END_DATE = "endDate";

	private final MongoCriteriaFactory mongoCriteriaFactory;
	private final MongoSortFactory mongoSortFactory;

	LabMongoQueryFactory(MongoCriteriaFactory mongoCriteriaFactory, MongoSortFactory mongoSortFactory) {
		this.mongoCriteriaFactory = mongoCriteriaFactory;
		this.mongoSortFactory = mongoSortFactory;
	}

	Query createQuery(com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate) {
		Query query = new Query(createCriteria(criteria, referenceDate));
		return mongoSortFactory.addSortAndLimit(query, criteria, FIELD_START_DATE);
	}

	private Criteria createCriteria(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate) {
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(mongoCriteriaFactory.createSubjectCriterion(criteria.subject()));
		mongoCriteriaFactory.createConceptCriterion(criteria, FIELD_CONCEPT)
				.ifPresent(criteriaList::add);
		mongoCriteriaFactory.createExcludeConceptsCriterion(criteria, FIELD_CONCEPT)
				.ifPresent(criteriaList::add);
		mongoCriteriaFactory.createFreshnessCriterion(criteria, referenceDate, FIELD_START_DATE, FIELD_END_DATE)
				.ifPresent(criteriaList::add);
		return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
	}
}

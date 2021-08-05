package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoSortFactory;

@Component
public class VitalSignMongoQueryFactory {
	private static final String FIELD_CONCEPT = "concept.concept";
	private static final String FIELD_DATE = "effectiveDateTime";

	private final MongoCriteriaFactory mongoCriteriaFactory;
	private final MongoSortFactory mongoSortFactory;

	VitalSignMongoQueryFactory(MongoCriteriaFactory mongoCriteriaFactory, MongoSortFactory mongoSortFactory) {
		this.mongoCriteriaFactory = mongoCriteriaFactory;
		this.mongoSortFactory = mongoSortFactory;
	}

	Query createQuery(com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate) {
		Query query = new Query(createCriteria(criteria, referenceDate));
		return mongoSortFactory.addSortAndLimit(query, criteria, FIELD_DATE);
	}

	private Criteria createCriteria(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate) {
		return mongoCriteriaFactory.createAll(criteria, referenceDate, FIELD_CONCEPT, FIELD_DATE);
	}
}

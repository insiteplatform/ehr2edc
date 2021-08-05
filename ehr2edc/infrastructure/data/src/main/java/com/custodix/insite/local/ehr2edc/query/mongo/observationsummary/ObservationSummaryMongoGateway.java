package com.custodix.insite.local.ehr2edc.query.mongo.observationsummary;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryEHRGateway;
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryItem;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Repository
class ObservationSummaryMongoGateway implements ObservationSummaryEHRGateway {

	private final MongoTemplate mongoTemplate;

	ObservationSummaryMongoGateway(@Qualifier("ehr2edcMongoQueryMongoTemplate") MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<ObservationSummaryItem> findForSubject(SubjectId subjectId) {
		Query query = new Query();
		query.addCriteria(where("subjectId").is(subjectId.getId()));
		return mongoTemplate.find(query, ObservationSummaryItem.class, "ObservationSummary");
	}
}
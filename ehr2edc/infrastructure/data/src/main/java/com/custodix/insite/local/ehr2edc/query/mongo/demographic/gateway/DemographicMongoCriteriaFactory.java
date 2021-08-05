package com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;

@Component
class DemographicMongoCriteriaFactory {
	private static final String FIELD_DEMOGRAPHIC_TYPE = "demographicType";
	private final MongoCriteriaFactory mongoCriteriaFactory;

	DemographicMongoCriteriaFactory(MongoCriteriaFactory mongoCriteriaFactory) {
		this.mongoCriteriaFactory = mongoCriteriaFactory;
	}

	Criteria createCriteria(com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria) {
		List<Criteria> criteriaList = new ArrayList<>();
		addSubjectCriterion(criteriaList, criteria);
		addTypeCriterion(criteriaList, criteria);
		return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
	}

	private void addSubjectCriterion(List<Criteria> criteriaList,
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria) {
		criteriaList.add(mongoCriteriaFactory.createSubjectCriterion(criteria.subject()));
	}

	private void addTypeCriterion(List<Criteria> criteriaList,
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria) {
		criteria.demographicType()
				.ifPresent(c -> addTypeCriterion(criteriaList, c));
	}

	private void addTypeCriterion(List<Criteria> criteriaList, DemographicTypeCriterion demographicTypeCriterion) {
		Criteria criterion = where(FIELD_DEMOGRAPHIC_TYPE).is(demographicTypeCriterion.getDemographicType());
		criteriaList.add(criterion);
	}
}

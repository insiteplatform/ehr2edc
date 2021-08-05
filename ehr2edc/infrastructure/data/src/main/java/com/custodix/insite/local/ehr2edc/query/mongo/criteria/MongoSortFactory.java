package com.custodix.insite.local.ehr2edc.query.mongo.criteria;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion;

@Component
public class MongoSortFactory {

	public Query addSortAndLimit(Query query, Criteria criteria, String dateField) {
		return this.createOrdinalSort(criteria, dateField)
				.map(query::with)
				.map(q -> q.limit(1))
				.orElse(query);
	}

	private Optional<Sort> createOrdinalSort(Criteria criteria, String dateField) {
		return criteria.ordinal()
				.map(ordinal -> doCreateOrdinalSort(ordinal, dateField));
	}

	private Sort doCreateOrdinalSort(OrdinalCriterion ordinalCriterion, String dateField) {
		Sort.Direction direction = ordinalCriterion.getOrdinal()
				.equals(OrdinalCriterion.Ordinal.FIRST) ? Sort.Direction.ASC : Sort.Direction.DESC;
		return new Sort(direction, dateField);
	}
}

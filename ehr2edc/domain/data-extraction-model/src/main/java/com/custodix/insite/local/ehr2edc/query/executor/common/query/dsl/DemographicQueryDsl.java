package com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class DemographicQueryDsl {
	private DemographicQuery query;

	DemographicQueryDsl() {
		this.query = new DemographicQuery();
	}

	public DemographicQueryDsl forSubject(SubjectId subjectId) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId));
	}

	public DemographicQueryDsl forType(DemographicType type) {
		return withCriterion(DemographicTypeCriterion.type(type));
	}

	public DemographicQuery getQuery() {
		return query;
	}

	private DemographicQueryDsl withCriterion(Criterion criterion) {
		this.query.addCriterion(criterion);
		return this;
	}
}

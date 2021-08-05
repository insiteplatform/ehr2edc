package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQueryObjectMother;

public class QueryObjectMother {

	public static Query anUnknownQuery() {
		return new UnknownQuery();
	}

	public static Query aDefaultQuery() {
		return DemographicQueryObjectMother.aDefaultDemographicQuery();
	}

	static class UnknownQuery implements Query {

		@Override
		public Criteria getCriteria() {
			throw new UnsupportedOperationException("This an unknown query. this operation is not supported");
		}

		@Override
		public void addCriterion(Criterion criterion) {
			throw new UnsupportedOperationException("This an unknown query. this operation is not supported");
		}

		@Override
		public Query forSubject(SubjectCriterion subjectCriterion) {
			throw new UnsupportedOperationException("This an unknown query. this operation is not supported");
		}
	}
}

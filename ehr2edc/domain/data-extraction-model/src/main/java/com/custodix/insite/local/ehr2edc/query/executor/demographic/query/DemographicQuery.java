package com.custodix.insite.local.ehr2edc.query.executor.demographic.query;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;

public class DemographicQuery implements Query<Demographics> {

	private final Criteria criteria;

	public DemographicQuery() {
		criteria = new Criteria();
	}

	private DemographicQuery(Criteria criteria) {
		this.criteria = criteria;
	}

	public void addCriterion(Criterion criterion) {
		criteria.add(criterion);
	}

	@Override
	public Criteria getCriteria() {
		return criteria;
	}

	@Override
	public Query<Demographics> forSubject(SubjectCriterion subjectCriterion) {
		DemographicQuery query = new DemographicQuery(this.getCriteria());
		query.addCriterion(subjectCriterion);
		return query;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final DemographicQuery that = (DemographicQuery) o;
		return Objects.equals(criteria, that.criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(criteria);
	}
}

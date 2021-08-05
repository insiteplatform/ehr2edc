package com.custodix.insite.local.ehr2edc.query.executor.medication.query;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;

public final class MedicationQuery implements Query<Medications> {

	private final Criteria criteria;

	public MedicationQuery() {
		this.criteria = new Criteria();
	}

	private MedicationQuery(Criteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Criteria getCriteria() {
		return criteria;
	}

	@Override
	public void addCriterion(Criterion criterion) {
		criteria.add(criterion);
	}

	@Override
	public Query<Medications> forSubject(SubjectCriterion subjectCriterion) {
		MedicationQuery query = new MedicationQuery(this.getCriteria());
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
		final MedicationQuery that = (MedicationQuery) o;
		return Objects.equals(criteria, that.criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(criteria);
	}
}

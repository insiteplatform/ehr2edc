package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;

public interface Query<T> {

	Criteria getCriteria();

	void addCriterion(Criterion criterion);

	Query<T> forSubject(SubjectCriterion subjectCriterion);
}

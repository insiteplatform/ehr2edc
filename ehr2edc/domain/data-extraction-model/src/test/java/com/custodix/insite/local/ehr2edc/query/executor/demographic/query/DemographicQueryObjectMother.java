package com.custodix.insite.local.ehr2edc.query.executor.demographic.query;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;

public class DemographicQueryObjectMother {

	public static DemographicQuery aDefaultDemographicQuery() {
		DemographicQuery demographicQuery = new DemographicQuery();
		demographicQuery.forSubject(SubjectCriterion.subjectIs("subjectId-123"));

		return demographicQuery;
	}

	public static DemographicQuery aDemographicQueryWithoutCriterion() {
		DemographicQuery demographicQuery = new DemographicQuery();
		return demographicQuery;
	}
}
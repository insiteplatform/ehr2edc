package com.custodix.insite.local.ehr2edc.query.executor.demographic.query;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;

public class Demographics implements QueryResult {
	private final List<Demographic> demographicList;

	public Demographics(final List<Demographic> observations) {
		this.demographicList = unmodifiableList(observations);
	}

	public List<Demographic> getDemographics() {
		return demographicList;
	}

	public Optional<Demographic> findFirstResultFor(DemographicType demographicType) {
		return demographicList.stream()
				.filter(demographic -> demographic.getDemographicType()
						.equals(demographicType))
				.findFirst();
	}

	@Override
	public List<DataPoint> getResults() {
		return new ArrayList<>(demographicList);
	}
}
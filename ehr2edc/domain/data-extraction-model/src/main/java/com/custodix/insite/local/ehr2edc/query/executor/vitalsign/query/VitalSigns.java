package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.google.common.collect.ImmutableList;

public class VitalSigns implements QueryResult {
	private final List<VitalSign> values;

	public VitalSigns(final List<VitalSign> values) {
		this.values = ImmutableList.copyOf(values);
	}

	public List<VitalSign> getValues() {
		return values;
	}

	@Override
	public List<DataPoint> getResults() {
		return new ArrayList<>(values);
	}
}
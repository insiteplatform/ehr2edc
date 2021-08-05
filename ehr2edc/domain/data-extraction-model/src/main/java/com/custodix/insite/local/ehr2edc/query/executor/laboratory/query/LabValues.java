package com.custodix.insite.local.ehr2edc.query.executor.laboratory.query;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.google.common.collect.ImmutableList;

public class LabValues implements QueryResult {
	private final List<LabValue> values;

	public LabValues(final List<LabValue> observations) {
		this.values = ImmutableList.copyOf(observations);
	}

	public List<LabValue> getValues() {
		return values;
	}

	@Override
	public List<DataPoint> getResults() {
		return new ArrayList<>(values);
	}
}
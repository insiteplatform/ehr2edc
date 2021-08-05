package com.custodix.insite.local.ehr2edc.query.executor.medication.query;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.google.common.collect.ImmutableList;

public class Medications implements QueryResult {
	private final List<Medication> values;

	public Medications(final List<Medication> values) {
		this.values = ImmutableList.copyOf(values);
	}

	public List<Medication> getValues() {
		return values;
	}

	@Override
	public List<DataPoint> getResults() {
		return new ArrayList<>(values);
	}
}
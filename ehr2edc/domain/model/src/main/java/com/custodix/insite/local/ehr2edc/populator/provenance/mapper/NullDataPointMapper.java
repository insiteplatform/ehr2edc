package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;

public class NullDataPointMapper implements DataPointMapper<ProvenanceDataPoint> {
	@Override
	public boolean supports(DataPoint dataPoint) {
		return dataPoint == null;
	}

	@Override
	public ProvenanceDataPoint map(DataPoint dataPoint) {
		return null;
	}
}

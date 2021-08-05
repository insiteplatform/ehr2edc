package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;

public interface DataPointMapper<R> {
	boolean supports(DataPoint dataPoint);

	R map(DataPoint dataPoint);
}

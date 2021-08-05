package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceLabValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public class ProvenanceLabValueMapper implements DataPointMapper<ProvenanceDataPoint> {
	@Override
	public boolean supports(DataPoint dataPoint) {
		return dataPoint instanceof LabValue;
	}

	@Override
	public ProvenanceDataPoint map(DataPoint dataPoint) {
		return ProvenanceLabValue.from((LabValue) dataPoint);
	}
}

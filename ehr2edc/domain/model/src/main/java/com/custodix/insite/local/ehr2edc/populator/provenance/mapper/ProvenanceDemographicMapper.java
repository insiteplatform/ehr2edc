package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;

public class ProvenanceDemographicMapper implements DataPointMapper<ProvenanceDataPoint> {
	@Override
	public boolean supports(DataPoint dataPoint) {
		return dataPoint instanceof Demographic;
	}

	@Override
	public ProvenanceDataPoint map(DataPoint dataPoint) {
		return ProvenanceDemographic.from((Demographic) dataPoint);
	}
}

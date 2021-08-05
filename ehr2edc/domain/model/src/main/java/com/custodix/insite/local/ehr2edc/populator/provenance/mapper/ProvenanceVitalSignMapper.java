package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public class ProvenanceVitalSignMapper implements DataPointMapper<ProvenanceDataPoint> {
	@Override
	public boolean supports(DataPoint dataPoint) {
		return dataPoint instanceof VitalSign;
	}

	@Override
	public ProvenanceDataPoint map(DataPoint dataPoint) {
		return ProvenanceVitalSign.from((VitalSign) dataPoint);
	}
}

package com.custodix.insite.local.ehr2edc.populator.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedication;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public class ProvenanceMedicationMapper implements DataPointMapper<ProvenanceDataPoint> {
	@Override
	public boolean supports(DataPoint dataPoint) {
		return dataPoint instanceof Medication;
	}

	@Override
	public ProvenanceDataPoint map(DataPoint dataPoint) {
		return ProvenanceMedication.from((Medication) dataPoint);
	}
}

package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;

public final class NullProvenanceDataPointMapper implements ProvenanceDataPointMapper<ProvenanceDataPointDocument> {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint == null;
	}

	@Override
	public ProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		return null;
	}
}

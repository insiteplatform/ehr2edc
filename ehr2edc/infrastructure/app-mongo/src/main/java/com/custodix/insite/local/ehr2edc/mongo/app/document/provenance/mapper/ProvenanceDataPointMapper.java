package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;

public interface ProvenanceDataPointMapper<R> {
	boolean supports(ProvenanceDataPoint provenanceDataPoint);

	R map(ProvenanceDataPoint provenanceDataPoint);
}

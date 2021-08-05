package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDemographicDocument;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceDemographicDocumentMapper
		implements ProvenanceDataPointMapper<ProvenanceDataPointDocument> {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceDemographic;
	}

	@Override
	public ProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return ProvenanceDemographicDocument.toDocument((ProvenanceDemographic) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}
}

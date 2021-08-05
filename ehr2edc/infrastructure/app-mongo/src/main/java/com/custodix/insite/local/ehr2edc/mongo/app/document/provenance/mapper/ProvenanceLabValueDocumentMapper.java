package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceLabValueDocument;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceLabValue;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceLabValueDocumentMapper
		implements ProvenanceDataPointMapper<ProvenanceDataPointDocument> {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceLabValue;
	}

	@Override
	public ProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return ProvenanceLabValueDocument.toDocument((ProvenanceLabValue) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}
}

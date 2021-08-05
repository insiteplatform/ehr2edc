package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceVitalSignDocument;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSign;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceVitalSignDocumentMapper
		implements ProvenanceDataPointMapper<ProvenanceDataPointDocument> {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceVitalSign;
	}

	@Override
	public ProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return ProvenanceVitalSignDocument.toDocument((ProvenanceVitalSign) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}
}

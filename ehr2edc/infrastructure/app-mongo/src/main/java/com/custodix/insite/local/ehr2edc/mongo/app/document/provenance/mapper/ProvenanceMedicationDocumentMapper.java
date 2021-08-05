package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceMedicationDocument;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedication;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceMedicationDocumentMapper
		implements ProvenanceDataPointMapper<ProvenanceDataPointDocument> {
	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceMedication;
	}

	@Override
	public ProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return ProvenanceMedicationDocument.toDocument((ProvenanceMedication) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}
}

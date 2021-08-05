package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;

public interface ProvenanceDataPointDocument {
	ProvenanceDataPoint restore();
}

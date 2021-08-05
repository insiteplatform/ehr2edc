package com.custodix.insite.local.ehr2edc.usecase.impl.getsubmitteditemprovenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;

class DemographicResponseMapper implements ResponseMapper {
	private static final String LABEL_DEMOGRAPHIC = "demographic";
	private static final String LABEL_VALUE = "value";

	@Override
	public boolean supports(ProvenanceDataPoint submittedProvenanceDataPoint) {
		return submittedProvenanceDataPoint instanceof ProvenanceDemographic;
	}

	@Override
	public GetSubmittedItemProvenance.Response map(ProvenanceDataPoint submittedProvenanceDataPoint) {
		ProvenanceDemographic provenanceDemographic = (ProvenanceDemographic) submittedProvenanceDataPoint;
		List<GetSubmittedItemProvenance.ProvenanceItem> items = new ArrayList<>();
		addItem(items, LABEL_DEMOGRAPHIC, provenanceDemographic.getDemographicType());
		addItem(items, LABEL_VALUE, provenanceDemographic.getValue());
		return GetSubmittedItemProvenance.Response.newBuilder()
				.withItems(items)
				.build();
	}
}

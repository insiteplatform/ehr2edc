package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;

class DemographicResponseMapper implements ResponseMapper {
	private static final String LABEL_DEMOGRAPHIC = "demographic";
	private static final String LABEL_VALUE = "value";

	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceDemographic;
	}

	@Override
	public GetItemProvenance.Response map(ProvenanceDataPoint provenanceDataPoint) {
		ProvenanceDemographic provenanceDemographic = (ProvenanceDemographic) provenanceDataPoint;
		List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
		addItem(items, LABEL_DEMOGRAPHIC, provenanceDemographic.getDemographicType());
		addItem(items, LABEL_VALUE, provenanceDemographic.getValue());
		return GetItemProvenance.Response.newBuilder()
				.withItems(items)
				.build();
	}
}

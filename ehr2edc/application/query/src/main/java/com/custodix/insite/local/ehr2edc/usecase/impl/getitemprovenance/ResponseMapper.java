package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;

interface ResponseMapper {
	boolean supports(ProvenanceDataPoint provenanceDataPoint);

	GetItemProvenance.Response map(ProvenanceDataPoint provenanceDataPoint);

	default void addGroup(List<GetItemProvenance.ProvenanceGroup> groups, String label,
			List<GetItemProvenance.ProvenanceItem> items) {
		if (!items.isEmpty()) {
			groups.add(new GetItemProvenance.ProvenanceGroup(label, items));
		}
	}

	default void addItem(List<GetItemProvenance.ProvenanceItem> items, String label, Object value) {
		if (value != null) {
			items.add(new GetItemProvenance.ProvenanceItem(label, value.toString()));
		}
	}
}

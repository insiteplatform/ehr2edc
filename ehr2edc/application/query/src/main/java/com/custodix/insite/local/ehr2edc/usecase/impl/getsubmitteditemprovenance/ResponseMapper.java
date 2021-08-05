package com.custodix.insite.local.ehr2edc.usecase.impl.getsubmitteditemprovenance;

import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;

interface ResponseMapper {
	boolean supports(ProvenanceDataPoint provenanceDataPoint);

	GetSubmittedItemProvenance.Response map(ProvenanceDataPoint provenanceDataPoint);

	default void addGroup(List<GetSubmittedItemProvenance.ProvenanceGroup> groups, String label,
                          List<GetSubmittedItemProvenance.ProvenanceItem> items) {
		if (!items.isEmpty()) {
			groups.add(new GetSubmittedItemProvenance.ProvenanceGroup(label, items));
		}
	}

	default void addItem(List<GetSubmittedItemProvenance.ProvenanceItem> items, String label, Object value) {
		if (value != null) {
			items.add(new GetSubmittedItemProvenance.ProvenanceItem(label, value.toString()));
		}
	}
}

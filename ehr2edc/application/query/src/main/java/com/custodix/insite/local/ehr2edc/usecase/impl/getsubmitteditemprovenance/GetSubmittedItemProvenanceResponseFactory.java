package com.custodix.insite.local.ehr2edc.usecase.impl.getsubmitteditemprovenance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

@Component
class GetSubmittedItemProvenanceResponseFactory {
	private final List<ResponseMapper> mappers;

	GetSubmittedItemProvenanceResponseFactory() {
		this.mappers = new ArrayList<>();
		mappers.add(new DemographicResponseMapper());
		mappers.add(new LabValueResponseMapper());
		mappers.add(new VitalSignResponseMapper());
		mappers.add(new MedicationResponseMapper());
	}

	GetSubmittedItemProvenance.Response create(ProvenanceDataPoint submittedProvenanceDataPoint) {
		ResponseMapper mapper = mappers.stream()
				.filter(m -> m.supports(submittedProvenanceDataPoint))
				.findFirst()
				.orElseThrow(() -> new SystemException("No response mapper found for " + submittedProvenanceDataPoint));
		return mapper.map(submittedProvenanceDataPoint);
	}
}

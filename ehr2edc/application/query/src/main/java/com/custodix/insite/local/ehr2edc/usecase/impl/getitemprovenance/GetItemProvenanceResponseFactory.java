package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

@Component
class GetItemProvenanceResponseFactory {
	private final List<ResponseMapper> mappers;

	GetItemProvenanceResponseFactory() {
		this.mappers = new ArrayList<>();
		mappers.add(new DemographicResponseMapper());
		mappers.add(new LabValueResponseMapper());
		mappers.add(new VitalSignResponseMapper());
		mappers.add(new MedicationResponseMapper());
	}

	GetItemProvenance.Response create(ProvenanceDataPoint provenanceDataPoint) {
		ResponseMapper mapper = mappers.stream()
				.filter(m -> m.supports(provenanceDataPoint))
				.findFirst()
				.orElseThrow(() -> new SystemException("No response mapper found for " + provenanceDataPoint));
		return mapper.map(provenanceDataPoint);
	}
}

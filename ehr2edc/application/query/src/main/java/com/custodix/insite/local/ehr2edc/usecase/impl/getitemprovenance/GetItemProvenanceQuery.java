package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;

@Query
class GetItemProvenanceQuery implements GetItemProvenance {
	private final PopulatedEventRepository populatedEventRepository;
	private final GetItemProvenanceResponseFactory responseFactory;

	GetItemProvenanceQuery(PopulatedEventRepository populatedEventRepository, GetItemProvenanceResponseFactory responseFactory) {
		this.populatedEventRepository = populatedEventRepository;
		this.responseFactory = responseFactory;
	}

	@Override
	public Response get(Request request) {
		PopulatedEvent populatedEvent = populatedEventRepository.getEvent(request.getEventId());
		ProvenanceDataPoint provenanceDataPoint = populatedEvent.getItemProvenance(request.getItemId());
		return responseFactory.create(provenanceDataPoint);
	}
}

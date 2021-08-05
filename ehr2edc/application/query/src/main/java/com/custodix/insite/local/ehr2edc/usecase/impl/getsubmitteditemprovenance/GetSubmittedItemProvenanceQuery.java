package com.custodix.insite.local.ehr2edc.usecase.impl.getsubmitteditemprovenance;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;

@Query
class GetSubmittedItemProvenanceQuery implements GetSubmittedItemProvenance {
	private final SubmittedEventRepository submittedEventRepository;
	private final GetSubmittedItemProvenanceResponseFactory responseFactory;

	GetSubmittedItemProvenanceQuery(SubmittedEventRepository submittedEventRepository,
                                   GetSubmittedItemProvenanceResponseFactory responseFactory) {
		this.submittedEventRepository = submittedEventRepository;
		this.responseFactory = responseFactory;
	}

	@Override
	public Response get(Request request) {
		SubmittedEvent submittedEvent = submittedEventRepository.get(request.getSubmittedEventId());
		ProvenanceDataPoint provenance = submittedEvent.getSubmittedItemProvenance(request.getSubmittedItemId());
		return responseFactory.create(provenance);
	}
}

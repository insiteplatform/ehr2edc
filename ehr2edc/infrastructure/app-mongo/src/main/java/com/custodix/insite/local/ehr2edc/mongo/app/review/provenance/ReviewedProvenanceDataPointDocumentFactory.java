package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public class ReviewedProvenanceDataPointDocumentFactory {
	private final List<ReviewedProvenanceDataPointMapper> mappers;

	public ReviewedProvenanceDataPointDocumentFactory(List<ReviewedProvenanceDataPointMapper> mappers) {
		this.mappers = mappers;
	}

	public ReviewedProvenanceDataPointDocument create(ProvenanceDataPoint provenanceDataPoint) {
		ReviewedProvenanceDataPointMapper mapper = mappers.stream()
				.filter(m -> m.supports(provenanceDataPoint))
				.findFirst()
				.orElseThrow(() -> new SystemException(
						"No provenance document mapper found for " + provenanceDataPoint));
		return mapper.map(provenanceDataPoint);
	}
}

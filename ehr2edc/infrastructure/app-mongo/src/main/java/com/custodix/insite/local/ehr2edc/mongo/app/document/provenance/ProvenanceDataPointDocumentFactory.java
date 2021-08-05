package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper.*;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceDataPointDocumentFactory {
	private static final List<ProvenanceDataPointMapper<ProvenanceDataPointDocument>> MAPPERS = getMappers();

	private ProvenanceDataPointDocumentFactory() {
	}

	public static ProvenanceDataPointDocument create(ProvenanceDataPoint provenanceDataPoint) {
		ProvenanceDataPointMapper<ProvenanceDataPointDocument> mapper = MAPPERS.stream()
				.filter(m -> m.supports(provenanceDataPoint))
				.findFirst()
				.orElseThrow(() -> new SystemException(
						"No provenance document mapper found for " + provenanceDataPoint));
		return mapper.map(provenanceDataPoint);
	}

	private static List<ProvenanceDataPointMapper<ProvenanceDataPointDocument>> getMappers() {
		List<ProvenanceDataPointMapper<ProvenanceDataPointDocument>> mappers = new ArrayList<>();
		mappers.add(new NullProvenanceDataPointMapper());
		mappers.add(new ProvenanceDemographicDocumentMapper());
		mappers.add(new ProvenanceLabValueDocumentMapper());
		mappers.add(new ProvenanceVitalSignDocumentMapper());
		mappers.add(new ProvenanceMedicationDocumentMapper());
		return mappers;
	}
}

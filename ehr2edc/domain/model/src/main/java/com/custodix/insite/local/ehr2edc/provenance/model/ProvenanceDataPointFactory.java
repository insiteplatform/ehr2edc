package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.provenance.mapper.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ProvenanceDataPointFactory {
	private static final List<DataPointMapper<ProvenanceDataPoint>> MAPPERS = getMappers();

	private ProvenanceDataPointFactory() {
	}

	public static ProvenanceDataPoint create(DataPoint dataPoint) {
		DataPointMapper<ProvenanceDataPoint> mapper = MAPPERS.stream()
				.filter(m -> m.supports(dataPoint))
				.findFirst()
				.orElseThrow(() -> new SystemException("No provenance mapper found for " + dataPoint));
		return mapper.map(dataPoint);
	}

	private static List<DataPointMapper<ProvenanceDataPoint>> getMappers() {
		List<DataPointMapper<ProvenanceDataPoint>> mappers = new ArrayList<>();
		mappers.add(new NullDataPointMapper());
		mappers.add(new ProvenanceDemographicMapper());
		mappers.add(new ProvenanceLabValueMapper());
		mappers.add(new ProvenanceVitalSignMapper());
		mappers.add(new ProvenanceMedicationMapper());
		return mappers;
	}
}

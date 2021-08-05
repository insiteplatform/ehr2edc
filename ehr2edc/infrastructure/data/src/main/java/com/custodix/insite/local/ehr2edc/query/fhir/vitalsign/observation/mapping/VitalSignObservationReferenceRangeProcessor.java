package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;

class VitalSignObservationReferenceRangeProcessor {
	static void processLowerLimit(List<Observation.ReferenceRange> referenceRanges,
			Consumer<BigDecimal> consumer) {
		process(referenceRanges, consumer, Observation.ReferenceRange::getLow);
	}

	static void processUpperLimit(List<Observation.ReferenceRange> referenceRanges,
			Consumer<BigDecimal> consumer) {
		process(referenceRanges, consumer, Observation.ReferenceRange::getHigh);
	}

	private static void process(List<Observation.ReferenceRange> referenceRanges, Consumer<BigDecimal> consumer,
			Function<Observation.ReferenceRange, SimpleQuantityDt> boundary) {
		referenceRanges.stream()
				.findFirst()
				.map(boundary)
				.map(SimpleQuantityDt::getValue)
				.ifPresent(consumer);
	}
}

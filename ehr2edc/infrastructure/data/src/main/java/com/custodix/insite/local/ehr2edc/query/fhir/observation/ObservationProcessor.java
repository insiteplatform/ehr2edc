package com.custodix.insite.local.ehr2edc.query.fhir.observation;

import java.util.stream.Stream;

import ca.uhn.fhir.model.dstu2.resource.Observation;

public interface ObservationProcessor {
	Stream<Observation> process(final Stream<Observation> observations);
}

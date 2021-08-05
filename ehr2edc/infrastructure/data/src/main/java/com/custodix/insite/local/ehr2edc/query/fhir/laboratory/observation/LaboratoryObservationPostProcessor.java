package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.observation.ObservationPostProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.observation.ObservationProcessor;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class LaboratoryObservationPostProcessor {
	private final IGenericClient client;

	LaboratoryObservationPostProcessor(IGenericClient client) {
		this.client = client;
	}

	Stream<Observation> process(Stream<Observation> observations, LaboratoryQuery query) {
		final ObservationPostProcessor postProcessor = new ObservationPostProcessor();
		final ObservationProcessor criteriaProcessor = postProcessor.getCriteriaProcessor(query.getCriteria());
		final ObservationProcessor specimenLoader = postProcessor.getSpecimenLoader(client);
		final Stream<ObservationProcessor> processors = Stream.of(criteriaProcessor, specimenLoader);
		return postProcessor.process(processors, observations);
	}
}

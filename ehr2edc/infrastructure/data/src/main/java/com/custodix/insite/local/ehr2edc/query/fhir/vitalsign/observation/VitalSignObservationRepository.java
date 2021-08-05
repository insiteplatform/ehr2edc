package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.FhirDstu2VitalSignResources;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class VitalSignObservationRepository {
	private final VitalSignObservationFhirQueryFactory observationFhirQueryFactory;

	VitalSignObservationRepository(VitalSignObservationFhirQueryFactory observationFhirQueryFactory) {
		this.observationFhirQueryFactory = observationFhirQueryFactory;
	}

	Stream<FhirDstu2VitalSignResources<Observation>> query(IGenericClient client, VitalSignQuery vitalSignQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		Stream<Observation> observations = getObservations(client, vitalSignQuery, referenceDate, patientResourceId);
		return observations.map(this::createFhirDstu2VitalSignResources);
	}

	private FhirDstu2VitalSignResources<Observation> createFhirDstu2VitalSignResources(Observation observation) {
		return FhirDstu2VitalSignResources.observationBuilder()
				.withVitalSignResource(observation)
				.build();
	}

	private Stream<Observation> getObservations(IGenericClient client, VitalSignQuery vitalSignQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		List<FhirQuery> fhirQueries = observationFhirQueryFactory.create(vitalSignQuery, patientResourceId,
				referenceDate);
		FhirResourceRepository<Observation> observationRepository = new FhirResourceRepository<>(client,
				Observation.class);
		Stream<Observation> observations = observationRepository.find(fhirQueries);
		return new VitalSignObservationPostProcessor().process(observations, vitalSignQuery);
	}
}

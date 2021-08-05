package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.FhirDstu2LaboratoryResources;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class LaboratoryObservationRepository {
	private final LaboratoryObservationFhirQueryFactory observationFhirQueryFactory;

	LaboratoryObservationRepository(LaboratoryObservationFhirQueryFactory observationFhirQueryFactory) {
		this.observationFhirQueryFactory = observationFhirQueryFactory;
	}

	Stream<FhirDstu2LaboratoryResources<Observation>> query(IGenericClient client, LaboratoryQuery laboratoryQuery,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		Stream<Observation> observations = getObservations(client, laboratoryQuery, referenceDate, patientResourceId);
		return observations.map(this::createFhirDstu2LaboratoryResources);
	}

	private FhirDstu2LaboratoryResources<Observation> createFhirDstu2LaboratoryResources(Observation observation) {
		return FhirDstu2LaboratoryResources.observationBuilder()
				.withLaboratoryResource(observation)
				.build();
	}

	private Stream<Observation> getObservations(IGenericClient client, LaboratoryQuery query,
			LocalDate referenceDate, FhirResourceId patientResourceId) {
		final FhirQuery fhirQuery = observationFhirQueryFactory.create(query, patientResourceId, referenceDate);
		final FhirResourceRepository<Observation> observationRepository =
				new FhirResourceRepository<>(client, Observation.class);
		final Stream<Observation> observations = observationRepository.find(fhirQuery);
		return new LaboratoryObservationPostProcessor(client).process(observations, query);
	}
}

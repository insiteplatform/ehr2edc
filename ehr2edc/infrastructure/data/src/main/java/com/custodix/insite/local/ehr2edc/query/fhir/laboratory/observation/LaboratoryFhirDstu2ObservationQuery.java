package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.FhirDstu2LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.FhirDstu2LaboratoryResources;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.client.api.IGenericClient;

class LaboratoryFhirDstu2ObservationQuery implements FhirDstu2LaboratoryQuery {

	private final LaboratoryObservationRepository observationRepository;
	private final LabValueFactoryForObservation labValueFactoryForObservation;

	LaboratoryFhirDstu2ObservationQuery(LaboratoryObservationRepository observationRepository,
			LabValueFactoryForObservation labValueFactoryForObservation) {
		this.observationRepository = observationRepository;
		this.labValueFactoryForObservation = labValueFactoryForObservation;
	}

	@Override
	public Stream<LabValue> query(IGenericClient client, LaboratoryQuery laboratoryQuery, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId) {
		Stream<FhirDstu2LaboratoryResources<Observation>> laboratoryResources = observationRepository.query(client,
				laboratoryQuery, referenceDate, fhirPatientResourceId);
		SubjectId subjectId = getSubjectId(laboratoryQuery);
		return laboratoryResources.map(
				laboratoryResource -> labValueFactoryForObservation.create(laboratoryResource, subjectId));
	}

	private SubjectId getSubjectId(LaboratoryQuery laboratoryQuery) {
		return laboratoryQuery.getCriteria()
				.subject()
				.getSubjectId();
	}
}

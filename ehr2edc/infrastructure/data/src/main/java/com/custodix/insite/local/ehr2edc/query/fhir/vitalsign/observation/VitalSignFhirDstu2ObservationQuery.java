package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.FhirDstu2VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping.VitalSignFactoryForObservation;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.rest.client.api.IGenericClient;

class VitalSignFhirDstu2ObservationQuery implements FhirDstu2VitalSignQuery {

	private final VitalSignObservationRepository observationRepository;
	private final VitalSignFactoryForObservation vitalSignFactoryForObservation;

	VitalSignFhirDstu2ObservationQuery(VitalSignObservationRepository observationRepository,
			VitalSignFactoryForObservation vitalSignFactoryForObservation) {
		this.observationRepository = observationRepository;
		this.vitalSignFactoryForObservation = vitalSignFactoryForObservation;
	}

	@Override
	public Stream<VitalSign> query(IGenericClient client, VitalSignQuery vitalSignQuery, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId) {
		SubjectId subjectId = getSubjectId(vitalSignQuery);
		return observationRepository.query(client, vitalSignQuery, referenceDate, fhirPatientResourceId)
				.flatMap(vitalSignResource -> vitalSignFactoryForObservation.create(vitalSignResource, subjectId));
	}

	private SubjectId getSubjectId(VitalSignQuery vitalSignQuery) {
		return vitalSignQuery.getCriteria()
				.subject()
				.getSubjectId();
	}
}

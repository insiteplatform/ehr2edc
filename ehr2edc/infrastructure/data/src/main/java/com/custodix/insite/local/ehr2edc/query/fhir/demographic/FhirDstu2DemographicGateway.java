package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

class FhirDstu2DemographicGateway implements EHRGateway<Demographics, DemographicQuery>, DataPointEHRGateway {
	private static final Logger LOGGER = LoggerFactory.getLogger(FhirDstu2DemographicGateway.class);

	private final IGenericClient client;
	private final FhirDstu2PatientRepository fhirDstu2PatientRepository;
	private final BiFunction<Patient, SubjectId, Optional<Demographic>> getDemographic;

	FhirDstu2DemographicGateway(IGenericClient client,
			BiFunction<Patient, SubjectId, Optional<Demographic>> getDemographic,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		this.getDemographic = getDemographic;
		this.client = client;
		this.fhirDstu2PatientRepository = fhirDstu2PatientRepository;
	}

	@Override
	public boolean canHandle(Query<?> query) {
		return query instanceof DemographicQuery;
	}

	@Override
	public Demographics execute(DemographicQuery query, LocalDate referenceDate) {
		Criteria criteria = query.getCriteria();
		Optional<Patient> patient = getPatient(criteria.subject());
		Optional<Demographic> demographic = patient.flatMap(p -> getDemographicBy(criteria, p));
		return new Demographics(demographic.map(Arrays::asList)
				.orElse(Collections.emptyList()));
	}

	private Optional<Patient> getPatient(SubjectCriterion subjectCriterion) {
		try {
			return findPatient(subjectCriterion);
		} catch (ResourceNotFoundException ex) {
			LOGGER.debug(String.format("Cannot find patient with id '%s' with fhir api",
					subjectCriterion.getPatientCDWReference()
							.getId()));
			return Optional.empty();
		}
	}

	private Optional<Patient> findPatient(SubjectCriterion subjectCriterion) {
		Optional<FhirResourceId> resourceId = fhirDstu2PatientRepository.findFhirResourceId(client,
				subjectCriterion.getPatientCDWReference());
		return resourceId.map(id -> client.read()
				.resource(Patient.class)
				.withId(id.getId())
				.execute());
	}

	private Optional<Demographic> getDemographicBy(Criteria criteria, Patient p) {
		return criteria.demographicType()
				.flatMap(t -> getDemographicBy(criteria.subject()
						.getSubjectId(), p));
	}

	private Optional<Demographic> getDemographicBy(SubjectId subjectId, Patient patient) {
		return getDemographic.apply(patient, subjectId);
	}

	@Override
	public List<String> findAllForSubject(SubjectId subjectId) {
		throw new UnsupportedOperationException("Not implemented");
	}
}

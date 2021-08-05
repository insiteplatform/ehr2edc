package com.custodix.insite.local.ehr2edc.query.fhir.patient;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirResourceRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;
import com.google.common.base.Strings;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class FhirDstu2PatientRepository {

	Stream<Patient> findPatient(final IGenericClient client, final PatientSearchCriteria patientSearchCriteria) {
		final FhirResourceRepository<Patient> repo = new FhirResourceRepository<>(client, Patient.class);
		final FhirQuery query = FhirQuery.newBuilder()
				.withCriteria(createCriteria(patientSearchCriteria))
				.build();
		return repo.find(query);
	}

	Stream<Patient> findPatients(final IGenericClient client, final String patientDomain,
			final String filter, int limit) {
		final Stream<ICriterion> identityCriteria =	getPatientDomainFilterCriteria(patientDomain);
		final Stream<ICriterion> contentCriteria = getContentFilterCriteria(filter);
		final Stream<ICriterion> criteria = Stream.of(identityCriteria, contentCriteria)
				.flatMap(Function.identity());
		final FhirQuery query = FhirQuery.newBuilder()
				.withCriteria(criteria.collect(Collectors.toList()))
				.withLimit(limit)
				.build();
		final FhirResourceRepository<Patient> repo = new FhirResourceRepository<>(client, Patient.class);
		return repo.find(query);
	}

	public Optional<FhirResourceId> findFhirResourceId(IGenericClient client, PatientCDWReference patientCDWReference) {
		final FhirResourceRepository<Patient> repo = new FhirResourceRepository<>(client, Patient.class);
		final FhirQuery query = FhirQuery.newBuilder()
				.withCriteria(asList(createIdentifierCriterion(patientCDWReference)))
				.build();
		final List<Patient> patients = repo.find(query).limit(2)
				.collect(Collectors.toList());
		if (patients.size() > 1) {
			throw DomainException.of("study.fhir.too.many.patient.found", patientCDWReference.getSource(),
					patientCDWReference.getId());
		}
		return patients.stream().findFirst()
				.map(patient -> patient.getId().getIdPart())
				.map(FhirResourceId::of);
	}

	private Stream<ICriterion> getPatientDomainFilterCriteria(final String patientDomain) {
		return Strings.isNullOrEmpty(patientDomain)? Stream.empty()
				: Stream.of(Patient.IDENTIFIER.hasSystemWithAnyCode(patientDomain));
	}

	private Stream<ICriterion> getContentFilterCriteria(final String contentFilter) {
		return Strings.isNullOrEmpty(contentFilter)? Stream.empty()
				: Stream.of(new StringClientParam(Constants.PARAM_CONTENT).matches().value(contentFilter));
	}

	private List<ICriterion> createCriteria(PatientSearchCriteria patientSearchCriteria) {
		return asList(
				createIdentifierCriterion(patientSearchCriteria.getPatientCDWReference())
				// Disable find with last name, first name and birth date  until is E2E-630 is finished
//				,
//				createLastNameCriterion(patientSearchCriteria.getLastName()),
//				createFirstNameCriterion(patientSearchCriteria.getFirstName()),
//				createFirstNameCriterion(patientSearchCriteria.getFirstName()),
//				createBirthDateCriterion(patientSearchCriteria.getBirthDate())
		);
	}

	private ICriterion<TokenClientParam> createIdentifierCriterion(PatientCDWReference patientCDWReference) {
		return Patient.IDENTIFIER.exactly()
				.systemAndIdentifier(patientCDWReference.getSource(), patientCDWReference.getId());
	}
//	 Disable find with last name, first name and birth date  until is E2E-630 is finished
//
//	private ICriterion<StringClientParam> createLastNameCriterion(String lastName) {
//		return Patient.FAMILY.matches().value(lastName);
//	}
//
//	private ICriterion<StringClientParam> createFirstNameCriterion(String firstName) {
//		return Patient.GIVEN.matches().value(firstName);
//	}
//
//	private DateClientParam.IDateCriterion createBirthDateCriterion(LocalDate birthDate) {
//		return Patient.BIRTHDATE.exactly().day(toDate(birthDate));
//	}
//
//	private Date toDate(LocalDate birthDate) {
//		return Date.from(birthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//	}
}

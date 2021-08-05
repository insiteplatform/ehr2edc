package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

class VitalSignObservationFhirQueryFactory {
	private static final String CATEGORY_VALUE = "vital-signs";
	private static final FhirQueryFactory.ResourceFields CORE_QUERY_FIELDS = createResourceFields(Observation.SP_CODE);
	private static final FhirQueryFactory.ResourceFields COMPONENT_CODE_QUERY_FIELD = createResourceFields(
			Observation.SP_COMPONENT_CODE);

	private final FhirQueryFactory fhirQueryFactory;

	VitalSignObservationFhirQueryFactory(FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	List<FhirQuery> create(VitalSignQuery query, FhirResourceId patientResourceId, LocalDate referenceDate) {
		FhirQuery coreQuery = createCoreQuery(query, patientResourceId, referenceDate);
		Optional<FhirQuery> componentCodeQuery = createComponentCodeQuery(query, patientResourceId, referenceDate);
		return Stream.of(Optional.of(coreQuery), componentCodeQuery)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	private FhirQuery createCoreQuery(VitalSignQuery query, FhirResourceId patientResourceId, LocalDate referenceDate) {
		FhirQuery fhirQuery = fhirQueryFactory.create(query, patientResourceId, referenceDate, CORE_QUERY_FIELDS,
				emptyList());
		List<ICriterion> criteria = new ArrayList<>(fhirQuery.getCriteria());
		createObservationCategoryCriterion(query).ifPresent(criteria::add);
		return FhirQuery.newBuilder(fhirQuery)
				.withCriteria(criteria)
				.build();
	}

	private Optional<FhirQuery> createComponentCodeQuery(VitalSignQuery query, FhirResourceId patientResourceId,
			LocalDate referenceDate) {
		return query.getCriteria()
				.concepts()
				.map(c -> fhirQueryFactory.create(query, patientResourceId, referenceDate, COMPONENT_CODE_QUERY_FIELD,
						emptyList()));
	}

	private Optional<ICriterion<TokenClientParam>> createObservationCategoryCriterion(VitalSignQuery query) {
		return query.getCriteria()
				.excludeConcepts()
				.map(c -> createObservationCategoryCriterion());
	}

	private ICriterion<TokenClientParam> createObservationCategoryCriterion() {
		return Observation.CATEGORY.exactly()
				.code(CATEGORY_VALUE);
	}

	private static FhirQueryFactory.ResourceFields createResourceFields(String codeField) {
		return FhirQueryFactory.ResourceFields.newBuilder()
				.withPatientField(FhirQueryFactory.ResourceField.of(Observation.SP_PATIENT))
				.withDateField(FhirQueryFactory.ResourceField.of(Observation.SP_DATE))
				.withCodeField(FhirQueryFactory.ResourceField.of(codeField))
				.build();
	}
}

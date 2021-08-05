package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

class LaboratoryObservationFhirQueryFactory {
	private static final String CATEGORY_VALUE = "laboratory";
	private static final FhirQueryFactory.ResourceFields FIELDS = FhirQueryFactory.ResourceFields.newBuilder()
			.withPatientField(FhirQueryFactory.ResourceField.of(Observation.SP_PATIENT))
			.withDateField(FhirQueryFactory.ResourceField.of(Observation.SP_DATE))
			.withCodeField(FhirQueryFactory.ResourceField.of(Observation.SP_CODE))
			.build();
	private static final List<Include> INCLUDES = singletonList(Observation.INCLUDE_SPECIMEN);

	private final FhirQueryFactory fhirQueryFactory;

	LaboratoryObservationFhirQueryFactory(FhirQueryFactory fhirQueryFactory) {
		this.fhirQueryFactory = fhirQueryFactory;
	}

	FhirQuery create(LaboratoryQuery query, FhirResourceId patientResourceId, LocalDate referenceDate) {
		FhirQuery fhirQuery = fhirQueryFactory.create(query, patientResourceId, referenceDate, FIELDS, INCLUDES);
		List<ICriterion> criteria = new ArrayList<>(fhirQuery.getCriteria());
		createObservationCategoryCriterion(query).ifPresent(criteria::add);
		return FhirQuery.newBuilder(fhirQuery)
				.withCriteria(criteria)
				.build();
	}

	private Optional<ICriterion<TokenClientParam>> createObservationCategoryCriterion(LaboratoryQuery query) {
		return query.getCriteria()
				.excludeConcepts()
				.map(c -> createObservationCategoryCriterion());
	}

	private ICriterion<TokenClientParam> createObservationCategoryCriterion() {
		return Observation.CATEGORY.exactly()
				.code(CATEGORY_VALUE);
	}
}

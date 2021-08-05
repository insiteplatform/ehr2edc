package com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl;

import java.time.Period;
import java.util.Arrays;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class MedicationQueryDsl {

	private MedicationQuery medicationQuery;

	MedicationQueryDsl() {
		this.medicationQuery = new MedicationQuery();
	}

	public MedicationQueryDsl forSubject(final SubjectId subjectId) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId));
	}

	public MedicationQueryDsl forSubject(final SubjectId subjectId, PatientCDWReference patientCDWReference) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId, patientCDWReference));
	}

	public MedicationQueryDsl forConcept(ConceptCode concept) {
		return withCriterion(ConceptCriterion.conceptIsExactly(concept));
	}

	public MedicationQueryDsl forConcepts(ConceptCode... concepts) {
		return withCriterion(ConceptCriterion.conceptIn(Arrays.asList(concepts)));
	}

	public MedicationQueryDsl forConceptsRelatedTo(final ConceptCode concept, final ConceptExpander conceptExpander) {
		return withCriterion(ConceptCriterion.conceptIsRelatedTo(concept, conceptExpander));
	}

	public MedicationQueryDsl excludingConcepts(ConceptCode... concepts) {
		return excludingConcepts(Arrays.asList(concepts));
	}

	MedicationQueryDsl excludingConcepts(List<ConceptCode> concepts) {
		return withCriterion(ExcludeConceptsCriterion.conceptNotIn(concepts));
	}

	public MedicationQueryDsl freshFor(Period period) {
		return withCriterion(FreshnessCriterion.maxAge(period));
	}

	public MedicationQueryDsl forOrdinal(OrdinalCriterion.Ordinal ordinal) {
		return withCriterion(OrdinalCriterion.ordinal(ordinal));
	}

	public MedicationQuery getQuery() {
		return medicationQuery;
	}

	private MedicationQueryDsl withCriterion(Criterion criterion) {
		this.medicationQuery.addCriterion(criterion);
		return this;
	}
}

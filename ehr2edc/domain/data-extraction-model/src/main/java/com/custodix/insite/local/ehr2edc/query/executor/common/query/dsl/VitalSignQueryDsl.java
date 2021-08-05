package com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl;

import java.time.Period;
import java.util.Arrays;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class VitalSignQueryDsl {

	private VitalSignQuery vitalSignQuery;

	VitalSignQueryDsl() {
		this.vitalSignQuery = new VitalSignQuery();
	}

	public VitalSignQueryDsl forSubject(final SubjectId subjectId) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId));
	}

	public VitalSignQueryDsl forSubject(final SubjectId subjectId, PatientCDWReference patientCDWReference) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId, patientCDWReference));
	}

	public VitalSignQueryDsl forConcept(ConceptCode concept) {
		return withCriterion(ConceptCriterion.conceptIsExactly(concept));
	}

	public VitalSignQueryDsl forConcepts(ConceptCode... concepts) {
		return forConcepts(Arrays.asList(concepts));
	}

	public VitalSignQueryDsl forConcepts(List<ConceptCode> concepts) {
		return withCriterion(ConceptCriterion.conceptIn(concepts));
	}

	public VitalSignQueryDsl forConcepts(List<ConceptCode> concepts, Boolean includeMissing) {
		return withCriterion(ConceptCriterion.conceptIn(concepts, includeMissing));
	}

	public VitalSignQueryDsl forConceptsRelatedTo(final ConceptCode concept, final ConceptExpander conceptExpander) {
		return withCriterion(ConceptCriterion.conceptIsRelatedTo(concept, conceptExpander));
	}

	public VitalSignQueryDsl excludingConcepts(ConceptCode... concepts) {
		return excludingConcepts(Arrays.asList(concepts));
	}

	public VitalSignQueryDsl excludingConcepts(List<ConceptCode> concepts) {
		return withCriterion(ExcludeConceptsCriterion.conceptNotIn(concepts));
	}

	public VitalSignQueryDsl freshFor(Period period) {
		return withCriterion(FreshnessCriterion.maxAge(period));
	}

	public VitalSignQueryDsl forOrdinal(OrdinalCriterion.Ordinal ordinal) {
		return withCriterion(OrdinalCriterion.ordinal(ordinal));
	}

	public VitalSignQuery getQuery() {
		return vitalSignQuery;
	}

	private VitalSignQueryDsl withCriterion(Criterion criterion) {
		this.vitalSignQuery.addCriterion(criterion);
		return this;
	}
}

package com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl;

import java.time.Period;
import java.util.Arrays;
import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class LabValueQueryDsl {
	private LaboratoryQuery observationQuery;

	LabValueQueryDsl() {
		this.observationQuery = new LaboratoryQuery();
	}

	public LabValueQueryDsl forSubject(SubjectId subjectId) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId));
	}

	public LabValueQueryDsl forSubject(final SubjectId subjectId, PatientCDWReference patientCDWReference) {
		return withCriterion(SubjectCriterion.subjectIs(subjectId, patientCDWReference));
	}

	public LabValueQueryDsl forConcept(ConceptCode concept) {
		return withCriterion(ConceptCriterion.conceptIsExactly(concept));
	}

	public LabValueQueryDsl forConcepts(ConceptCode... concepts) {
		return forConcepts(Arrays.asList(concepts));
	}

	public LabValueQueryDsl forConcepts(List<ConceptCode> concepts) {
		return withCriterion(ConceptCriterion.conceptIn(concepts));
	}

	public LabValueQueryDsl forConcepts(List<ConceptCode> concepts, Boolean includeMissing) {
		return withCriterion(ConceptCriterion.conceptIn(concepts, includeMissing));
	}

	public LabValueQueryDsl forConceptsRelatedTo(ConceptCode concept, ConceptExpander conceptExpander) {
		return withCriterion(ConceptCriterion.conceptIsRelatedTo(concept, conceptExpander));
	}

	public LabValueQueryDsl excludingConcepts(ConceptCode... concepts) {
		return excludingConcepts(Arrays.asList(concepts));
	}

	public LabValueQueryDsl excludingConcepts(List<ConceptCode> concepts) {
		return withCriterion(ExcludeConceptsCriterion.conceptNotIn(concepts));
	}

	public LabValueQueryDsl freshFor(Period period) {
		return withCriterion(FreshnessCriterion.maxAge(period));
	}

	public LabValueQueryDsl forOrdinal(OrdinalCriterion.Ordinal ordinal) {
		return withCriterion(OrdinalCriterion.ordinal(ordinal));
	}

	public LaboratoryQuery getQuery() {
		return observationQuery;
	}

	private LabValueQueryDsl withCriterion(Criterion criterion) {
		this.observationQuery.addCriterion(criterion);
		return this;
	}
}

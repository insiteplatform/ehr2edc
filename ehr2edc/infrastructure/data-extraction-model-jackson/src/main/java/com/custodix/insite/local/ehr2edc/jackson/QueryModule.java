package com.custodix.insite.local.ehr2edc.jackson;

import com.custodix.insite.local.ehr2edc.jackson.mixin.concept.CodedConceptMixin;
import com.custodix.insite.local.ehr2edc.jackson.mixin.criterium.*;
import com.custodix.insite.local.ehr2edc.jackson.mixin.query.QueryMixin;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class QueryModule {
	private QueryModule() {
	}

	public static SimpleModule create() {
		SimpleModule module = new SimpleModule();

		module.setMixInAnnotation(Query.class, QueryMixin.class);
		module.setMixInAnnotation(Criteria.class, CriteriaMixin.class);
		module.setMixInAnnotation(Criterion.class, CriterionMixin.class);
		module.setMixInAnnotation(SubjectCriterion.class, SubjectCriterionMixin.class);
		module.setMixInAnnotation(ConceptCriterion.class, ConceptCriterionMixin.class);
		module.setMixInAnnotation(ExcludeConceptsCriterion.class, ExcludeConceptsCriterionMixin.class);
		module.setMixInAnnotation(ConceptCode.class, CodedConceptMixin.class);
		module.setMixInAnnotation(FreshnessCriterion.class, FreshnessCriterionMixin.class);
		module.setMixInAnnotation(DemographicTypeCriterion.class, DemographicTypeCriterionMixin.class);
		module.setMixInAnnotation(OrdinalCriterion.class, OrdinalCriterionMixin.class);
		return module;
	}
}
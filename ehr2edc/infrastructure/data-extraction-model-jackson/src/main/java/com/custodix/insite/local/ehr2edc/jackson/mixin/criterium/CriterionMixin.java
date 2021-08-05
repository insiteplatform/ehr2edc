package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
			  include = JsonTypeInfo.As.PROPERTY,
			  property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = ConceptCriterion.class,
								   name = "includeConcepts"),
				@JsonSubTypes.Type(value = ConceptCriterion.class,
								   name = "concept"),
				@JsonSubTypes.Type(value = ExcludeConceptsCriterion.class,
								   name = "excludeConcepts"),
				@JsonSubTypes.Type(value = SubjectCriterion.class,
								   name = "subject"),
				@JsonSubTypes.Type(value = FreshnessCriterion.class,
								   name = "freshness"),
				@JsonSubTypes.Type(value = DemographicTypeCriterion.class,
								   name = "demographicType"),
				@JsonSubTypes.Type(value = OrdinalCriterion.class,
								   name = "ordinal") })
public abstract class CriterionMixin {
}

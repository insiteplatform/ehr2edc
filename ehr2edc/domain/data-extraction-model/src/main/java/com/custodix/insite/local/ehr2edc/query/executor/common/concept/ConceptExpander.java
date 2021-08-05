package com.custodix.insite.local.ehr2edc.query.executor.common.concept;

import java.util.List;

public interface ConceptExpander {
	List<ConceptCode> resolve(ConceptCode concept);
}

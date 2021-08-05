package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

public final class ExcludeConceptsCriterion implements Criterion {
	private final List<ConceptCode> concepts;

	private ExcludeConceptsCriterion(final List<ConceptCode> concepts) {
		this.concepts = concepts;
	}

	public static ExcludeConceptsCriterion conceptIsNot(final ConceptCode concept) {
		return new ExcludeConceptsCriterion(Collections.singletonList(concept));
	}

	public static ExcludeConceptsCriterion conceptNotIn(final List<ConceptCode> concept) {
		return new ExcludeConceptsCriterion(concept);
	}

	public List<ConceptCode> getConcepts() {
		return concepts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ExcludeConceptsCriterion that = (ExcludeConceptsCriterion) o;
		return Objects.equals(concepts, that.concepts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(concepts);
	}
}

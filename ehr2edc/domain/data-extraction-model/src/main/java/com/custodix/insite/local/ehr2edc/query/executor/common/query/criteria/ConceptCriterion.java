package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander;

public final class ConceptCriterion implements Criterion {
	private final List<ConceptCode> concepts;
	private final boolean includeMissing;

	private ConceptCriterion(final List<ConceptCode> concepts, boolean includeMissing) {
		this.concepts = concepts;
		this.includeMissing = includeMissing;
	}

	public static ConceptCriterion conceptIsExactly(final ConceptCode concept) {
		return conceptIn(Collections.singletonList(concept));
	}

	public static ConceptCriterion conceptIsRelatedTo(final ConceptCode concept,
			final ConceptExpander conceptExpander) {
		return conceptIn(conceptExpander.resolve(concept));
	}

	public static ConceptCriterion conceptIn(final List<ConceptCode> concept) {
		return new ConceptCriterion(concept, false);
	}

	public static ConceptCriterion conceptIn(final List<ConceptCode> concepts, boolean includeMissing) {
		return new ConceptCriterion(concepts, includeMissing);
	}

	public List<ConceptCode> getConcepts() {
		return concepts;
	}

	public boolean getIncludeMissing() {
		return includeMissing;
	}

	public String toString() {
		return concepts.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ConceptCriterion that = (ConceptCriterion) o;
		return Objects.equals(concepts, that.concepts) && Objects.equals(includeMissing, that.includeMissing);
	}

	@Override
	public int hashCode() {
		return Objects.hash(concepts, includeMissing);
	}
}

package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;

public final class Criteria {

	private static final Predicate<Criterion> IS_SUBJECT_CRITERION = SubjectCriterion.class::isInstance;
	private final Set<Criterion> criteria = new HashSet<>();

	public void add(Criterion criterion) {
		criteria.add(criterion);
	}

	public SubjectCriterion subject() {
		return criteriaOfType(SubjectCriterion.class).findFirst().orElseThrow(() -> new DomainException("The subject criterion is missing"));
	}

	public Optional<ConceptCriterion> concepts() {
		return criteriaOfType(ConceptCriterion.class).findFirst();
	}

	public Optional<ExcludeConceptsCriterion> excludeConcepts() {
		return criteriaOfType(ExcludeConceptsCriterion.class).findFirst();
	}

	public Optional<FreshnessCriterion> freshness() {
		return criteriaOfType(FreshnessCriterion.class).findFirst();
	}

	public Optional<DemographicTypeCriterion> demographicType() {
		return criteriaOfType(DemographicTypeCriterion.class).findFirst();
	}

	public Set<Criterion> queryFilters() {
		return criteria.stream()
				.filter(IS_SUBJECT_CRITERION.negate())
				.collect(Collectors.toSet());
	}

	public Optional<OrdinalCriterion> ordinal() {
		return criteriaOfType(OrdinalCriterion.class).findFirst();
	}

	private <T> Stream<T> criteriaOfType(Class<T> type) {
		return criteria.stream()
				.filter(type::isInstance)
				.map(type::cast);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Criteria criteria1 = (Criteria) o;
		return Objects.equals(criteria, criteria1.criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(criteria);
	}
}

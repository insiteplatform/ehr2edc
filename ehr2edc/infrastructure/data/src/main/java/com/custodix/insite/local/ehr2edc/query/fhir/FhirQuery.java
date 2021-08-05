package com.custodix.insite.local.ehr2edc.query.fhir;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.gclient.ICriterion;

public final class FhirQuery {
	private final List<ICriterion> criteria;
	private final SortSpec sortSpec;
	private final Integer limit;
	private final List<Include> includes;

	private FhirQuery(Builder builder) {
		criteria = builder.criteria;
		sortSpec = builder.sortSpec;
		limit = builder.limit;
		includes = builder.includes != null ? builder.includes : emptyList();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(FhirQuery copy) {
		Builder builder = new Builder();
		builder.criteria = copy.getCriteria();
		builder.sortSpec = copy.getSortSpec()
				.orElse(null);
		builder.limit = copy.getLimit()
				.orElse(null);
		builder.includes = copy.getIncludes();
		return builder;
	}

	public List<ICriterion> getCriteria() {
		return criteria;
	}

	public Optional<SortSpec> getSortSpec() {
		return Optional.ofNullable(sortSpec);
	}

	public Optional<Integer> getLimit() {
		return Optional.ofNullable(limit);
	}

	public List<Include> getIncludes() {
		return includes;
	}

	public static final class Builder {
		private List<ICriterion> criteria;
		private SortSpec sortSpec;
		private Integer limit;
		private List<Include> includes;

		private Builder() {
		}

		public Builder withCriteria(List<ICriterion> criteria) {
			this.criteria = criteria;
			return this;
		}

		public Builder withSortSpec(SortSpec sortSpec) {
			this.sortSpec = sortSpec;
			return this;
		}

		public Builder withLimit(Integer limit) {
			this.limit = limit;
			return this;
		}

		public Builder withIncludes(List<Include> val) {
			includes = val;
			return this;
		}

		public FhirQuery build() {
			return new FhirQuery(this);
		}
	}
}

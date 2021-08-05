package com.custodix.insite.local.ehr2edc.querymapping.model;

import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;

public final class ItemQueryMapping {
	private Query<?> query;
	private List<Projector> projectors;

	private ItemQueryMapping(final Builder builder) {
		query = builder.query;
		projectors = builder.projectors;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Query<?> getQuery() {
		return query;
	}

	public List<Projector> getProjectors() {
		return projectors;
	}

	public static final class Builder {
		private Query<?> query;
		private List<Projector> projectors;

		private Builder() {
		}

		public Builder withQuery(final Query<?> val) {
			query = val;
			return this;
		}

		public Builder withProjectors(final List<Projector> val) {
			projectors = val;
			return this;
		}

		public ItemQueryMapping build() {
			return new ItemQueryMapping(this);
		}
	}
}

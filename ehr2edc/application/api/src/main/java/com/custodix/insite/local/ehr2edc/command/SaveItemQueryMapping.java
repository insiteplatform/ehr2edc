package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface SaveItemQueryMapping {

	@Allow(ANYONE)
	void save(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@SynchronizationCorrelator
		@NotNull
		@Valid
		private StudyId studyId;
		@NotNull
		@Valid
		private ItemDefinitionId itemId;
		@NotNull
		private Query<?> query;
		@NotNull
		private List<Projector> projectors;

		private Request(final Builder builder) {
			studyId = builder.studyId;
			itemId = builder.itemId;
			query = builder.query;
			projectors = builder.projectors;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(final Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.getStudyId();
			builder.itemId = copy.getItemId();
			builder.query = copy.getQuery();
			builder.projectors = copy.getProjectors();
			return builder;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public ItemDefinitionId getItemId() {
			return itemId;
		}

		public Query<?> getQuery() {
			return query;
		}

		public List<Projector> getProjectors() {
			return projectors;
		}

		public static final class Builder {
			private ItemDefinitionId itemId;
			private StudyId studyId;
			private Query<?> query;
			private List<Projector> projectors;

			private Builder() {
			}

			public Builder withItemId(final ItemDefinitionId val) {
				itemId = val;
				return this;
			}

			public Builder withQuery(final Query<?> val) {
				query = val;
				return this;
			}

			public Builder withProjectors(final List<Projector> val) {
				projectors = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}

			public Builder withStudyId(final @NotNull @Valid StudyId val) {
				studyId = val;
				return this;
			}
		}
	}
}

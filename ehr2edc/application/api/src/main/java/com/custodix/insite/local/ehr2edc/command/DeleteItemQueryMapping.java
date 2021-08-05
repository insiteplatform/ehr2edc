package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public interface DeleteItemQueryMapping {

	@Allow(ANYONE)
	void delete(@Valid @NotNull Request request);

	final class Request {
		@SynchronizationCorrelator
		@Valid
		@NotNull
		private final StudyId studyId;
		@Valid
		@NotNull
		private final ItemDefinitionId itemId;

		private Request(Builder builder) {
			studyId = builder.studyId;
			itemId = builder.itemId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public ItemDefinitionId getItemId() {
			return itemId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private ItemDefinitionId itemId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withItemId(ItemDefinitionId itemId) {
				this.itemId = itemId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

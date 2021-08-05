package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface GetPopulatedEventHistory {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response get(@Valid Request request);

	final class Request {
		@NotNull
		@Valid
		@AuthorizationCorrelator
		private final SubjectId subjectId;
		@NotNull
		@Valid
		private final EventDefinitionId eventId;

		private Request(Builder builder) {
			subjectId = builder.subjectId;
			eventId = builder.eventId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EventDefinitionId getEventId() {
			return eventId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private EventDefinitionId eventId;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withEventId(EventDefinitionId val) {
				eventId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<PopulatedEventHistoryItem> historyItems;

		private Response(Builder builder) {
			historyItems = builder.historyItems;
		}

		public List<PopulatedEventHistoryItem> getHistoryItems() {
			return historyItems;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<PopulatedEventHistoryItem> historyItems;

			private Builder() {
			}

			public Builder withHistoryItems(List<PopulatedEventHistoryItem> val) {
				historyItems = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class PopulatedEventHistoryItem {
		private final EventId eventId;
		private final Instant populationTime;
		private final LocalDate referenceDate;
		private final String populator;

		private PopulatedEventHistoryItem(Builder builder) {
			eventId = builder.eventId;
			populationTime = builder.populationTime;
			referenceDate = builder.referenceDate;
			populator = builder.populator;
		}

		public EventId getEventId() {
			return eventId;
		}

		public Instant getPopulationTime() {
			return populationTime;
		}

		public LocalDate getReferenceDate() {
			return referenceDate;
		}

		public String getPopulator() {
			return populator;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private EventId eventId;
			private Instant populationTime;
			private LocalDate referenceDate;
			private String populator;

			private Builder() {
			}

			public Builder withEventId(EventId val) {
				eventId = val;
				return this;
			}

			public Builder withPopulationTime(Instant val) {
				populationTime = val;
				return this;
			}

			public Builder withReferenceDate(LocalDate val) {
				referenceDate = val;
				return this;
			}

			public Builder withPopulator(String val) {
				populator = val;
				return this;
			}

			public PopulatedEventHistoryItem build() {
				return new PopulatedEventHistoryItem(this);
			}
		}
	}
}
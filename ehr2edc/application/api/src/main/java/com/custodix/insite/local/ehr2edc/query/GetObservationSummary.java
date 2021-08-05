package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface GetObservationSummary {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response getSummary(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			subjectId = builder.subjectId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<SummaryItem> summaryItems;

		private Response(Builder builder) {
			summaryItems = builder.summaryItems;
		}

		public List<SummaryItem> getSummaryItems() {
			return summaryItems;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<SummaryItem> summaryItems;

			private Builder() {
			}

			public Builder withSummaryItems(List<SummaryItem> val) {
				summaryItems = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class SummaryItem {
		private final LocalDate date;
		private final int amountOfObservations;

		private SummaryItem(Builder builder) {
			date = builder.date;
			amountOfObservations = builder.amountOfObservations;
		}

		public LocalDate getDate() {
			return date;
		}

		public int getAmountOfObservations() {
			return amountOfObservations;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private LocalDate date;
			private int amountOfObservations;

			private Builder() {
			}

			public Builder withDate(LocalDate val) {
				date = val;
				return this;
			}

			public Builder withAmountOfObservations(int val) {
				amountOfObservations = val;
				return this;
			}

			public SummaryItem build() {
				return new SummaryItem(this);
			}
		}
	}
}
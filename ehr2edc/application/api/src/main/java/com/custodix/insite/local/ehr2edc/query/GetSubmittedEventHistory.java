package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface GetSubmittedEventHistory {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response get(@NotNull @Valid Request request);

	final class Request {
		@NotNull
		@Valid
		@AuthorizationCorrelator
		private final SubjectId subjectId;
		@NotNull
		@Valid
		private final EventId eventId;

		private Request(Builder builder) {
			subjectId = builder.subjectId;
			eventId = builder.eventId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EventId getEventId() {
			return eventId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private EventId eventId;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withEventId(EventId val) {
				eventId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<SubmittedEventHistoryItem> historyItems;

		private Response(Builder builder) {
			historyItems = builder.historyItems;
		}

		public List<SubmittedEventHistoryItem> getHistoryItems() {
			return historyItems;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<SubmittedEventHistoryItem> historyItems;

			private Builder() {
			}

			public Builder withHistoryItems(List<SubmittedEventHistoryItem> val) {
				historyItems = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class SubmittedEventHistoryItem {
		private final SubmittedEventId reviewedEventId;
		private final Instant reviewDateTime;
		private final UserIdentifier reviewer;

		private SubmittedEventHistoryItem(Builder builder) {
			reviewedEventId = builder.reviewedEventId;
			reviewDateTime = builder.reviewDateTime;
			reviewer = builder.reviewer;
		}

		public SubmittedEventId getReviewedEventId() {
			return reviewedEventId;
		}

		public Instant getReviewDateTime() {
			return reviewDateTime;
		}

		public UserIdentifier getReviewer() {
			return reviewer;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubmittedEventId reviewedEventId;
			private Instant reviewDateTime;
			private UserIdentifier reviewer;

			private Builder() {
			}

			public Builder withReviewedEventId(SubmittedEventId val) {
				reviewedEventId = val;
				return this;
			}

			public Builder withReviewDateTime(Instant val) {
				reviewDateTime = val;
				return this;
			}

			public Builder withReviewer(UserIdentifier val) {
				reviewer = val;
				return this;
			}

			public SubmittedEventHistoryItem build() {
				return new SubmittedEventHistoryItem(this);
			}
		}
	}
}
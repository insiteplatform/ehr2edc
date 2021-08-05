package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface PopulateEvent {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response populate(@NotNull @Valid Request request);

	final class Request {
		@NotNull
		@PastOrPresent
		private final LocalDate referenceDate;
		@AuthorizationCorrelator
		@Valid
		@NotNull
		private final StudyId studyId;
		@Valid
		@NotNull
		private final EventDefinitionId eventDefinitionId;
		@Valid
		@NotNull
		private final SubjectId subjectId;

		private Request(Builder builder) {
			referenceDate = builder.referenceDate;
			studyId = builder.studyId;
			eventDefinitionId = builder.eventDefinitionId;
			subjectId = builder.subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public LocalDate getReferenceDate() {
			return referenceDate;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public EventDefinitionId getEventDefinitionId() {
			return eventDefinitionId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.referenceDate = copy.getReferenceDate();
			builder.studyId = copy.getStudyId();
			builder.eventDefinitionId = copy.getEventDefinitionId();
			builder.subjectId = copy.getSubjectId();
			return builder;
		}

		public static final class Builder {
			private LocalDate referenceDate;
			private StudyId studyId;
			private EventDefinitionId eventDefinitionId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withReferenceDate(LocalDate referenceDate) {
				this.referenceDate = referenceDate;
				return this;
			}

			public Builder withStudyId(StudyId study) {
				this.studyId = study;
				return this;
			}

			public Builder withEventDefinitionId(EventDefinitionId event) {
				this.eventDefinitionId = event;
				return this;
			}

			public Builder withSubjectId(SubjectId subject) {
				this.subjectId = subject;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final long populatedDataPoints;

		private Response(Builder builder) {
			populatedDataPoints = builder.populatedDataPoints;
		}

		public long getPopulatedDataPoints() {
			return populatedDataPoints;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.populatedDataPoints = copy.getPopulatedDataPoints();
			return builder;
		}

		public static final class Builder {
			private long populatedDataPoints;

			private Builder() {
			}

			public Builder withPopulatedDataPoints(long populatedDataPoints) {
				this.populatedDataPoints = populatedDataPoints;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

}

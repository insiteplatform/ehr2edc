package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PopulationNotReadyReason;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface GetEventPopulationReadiness {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response getEventPopulationReadiness(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		@AuthorizationCorrelator
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			studyId = builder.studyId;
			subjectId = builder.subjectId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withStudyId(final StudyId val) {
				studyId = val;
				return this;
			}

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {

		private final boolean ready;
		private final PopulationNotReadyReason notReadyReason;
		private final boolean subjectMigrationInProgress;

		private Response(Builder builder) {
			ready = builder.ready;
			notReadyReason = builder.notReadyReason;
			subjectMigrationInProgress = builder.subjectMigrationInProgress;
		}

		public boolean isReady() {
			return ready;
		}

		public PopulationNotReadyReason getNotReadyReason() {
			return notReadyReason;
		}

		public boolean isSubjectMigrationInProgress() {
			return subjectMigrationInProgress;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private boolean ready;
			private PopulationNotReadyReason notReadyReason;
			private boolean subjectMigrationInProgress;

			private Builder() {
			}

			public Builder withReady(boolean val) {
				ready = val;
				return this;
			}

			public Builder withNotReadyReason(PopulationNotReadyReason val) {
				notReadyReason = val;
				return this;
			}

			public Builder withSubjectMigrationInProgress(boolean val) {
				subjectMigrationInProgress = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}

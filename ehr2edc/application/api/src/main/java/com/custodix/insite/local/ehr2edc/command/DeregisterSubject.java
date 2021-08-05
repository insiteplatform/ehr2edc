package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public interface DeregisterSubject {

	@Allow(ASSIGNED_INVESTIGATOR)
	void deregister(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@SynchronizationCorrelator
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;
		@NotNull
		private final LocalDate endDate;
		private final DeregisterReason dataCaptureStopReason;

		private Request(Builder builder) {
			studyId = builder.studyId;
			subjectId = builder.subjectId;
			endDate = builder.endDate;
			dataCaptureStopReason = builder.dataCaptureStopReason;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public DeregisterReason getDataCaptureStopReason() {
			return dataCaptureStopReason;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			builder.subjectId = copy.subjectId;
			builder.endDate = copy.endDate;
			builder.dataCaptureStopReason = copy.dataCaptureStopReason;
			return builder;
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private StudyId studyId;
			private SubjectId subjectId;
			private LocalDate endDate;
			private DeregisterReason dataCaptureStopReason;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Builder withEndDate(LocalDate endDate) {
				this.endDate = endDate;
				return this;
			}

			public Builder withDataCaptureStopReason(DeregisterReason dataCaptureStopReason) {
				this.dataCaptureStopReason = dataCaptureStopReason;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
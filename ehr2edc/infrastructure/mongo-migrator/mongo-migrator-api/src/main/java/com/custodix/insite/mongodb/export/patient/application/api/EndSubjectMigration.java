package com.custodix.insite.mongodb.export.patient.application.api;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface EndSubjectMigration {

	void end(@Valid Request request);

	final class Request {
		@Valid
		@NotNull
		private final SubjectId subjectId;
		@NotNull
		private final LocalDateTime endDate;

		private Request(final Builder builder) {
			subjectId = builder.subjectId;
			endDate = builder.endDate;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private LocalDateTime endDate;

			private Builder() {
			}

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withEndDate(final LocalDateTime val) {
				endDate = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

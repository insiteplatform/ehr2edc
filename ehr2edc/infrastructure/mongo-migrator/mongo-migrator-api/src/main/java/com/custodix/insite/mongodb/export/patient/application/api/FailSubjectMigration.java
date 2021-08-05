package com.custodix.insite.mongodb.export.patient.application.api;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface FailSubjectMigration {

	void fail(Request request);

	final class Request {
		@Valid
		@NotNull
		private final SubjectId subjectId;
		@NotNull
		private final LocalDateTime failDate;

		private Request(final Builder builder) {
			subjectId = builder.subjectId;
			failDate = builder.failDate;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDateTime getFailDate() {
			return failDate;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private LocalDateTime failDate;

			private Builder() {
			}

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withFailDate(final LocalDateTime val) {
				failDate = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

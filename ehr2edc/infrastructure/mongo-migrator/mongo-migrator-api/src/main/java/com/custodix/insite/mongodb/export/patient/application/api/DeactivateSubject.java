package com.custodix.insite.mongodb.export.patient.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface DeactivateSubject {
	void deactivate(@Valid Request request);

	final class Request {
		@Valid
		@NotNull
		private final SubjectId subjectId;

		private Request(final Builder builder) {
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

			public Builder withSubjectId(final @Valid @NotNull SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface InitiateRecovery {
	void initiateBySecurityQuestion(@Valid BySecurityQuestionRequest request);

	void initiateByAdmin(@Valid ByAdminRequest request);

	final class BySecurityQuestionRequest {
		@NotNull
		@Valid
		private final Email email;
		@NotBlank
		private final String questionAnswer;

		private BySecurityQuestionRequest(Builder builder) {
			email = builder.email;
			questionAnswer = builder.questionAnswer;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Email getEmail() {
			return email;
		}

		public String getQuestionAnswer() {
			return questionAnswer;
		}

		public static final class Builder {
			private Email email;
			private String questionAnswer;

			private Builder() {
			}

			public Builder withEmail(Email email) {
				this.email = email;
				return this;
			}

			public Builder withQuestionAnswer(String questionAnswer) {
				this.questionAnswer = questionAnswer;
				return this;
			}

			public BySecurityQuestionRequest build() {
				return new BySecurityQuestionRequest(this);
			}
		}
	}

	final class ByAdminRequest {
		@NotNull
		private final UserIdentifier userIdentifier;

		private ByAdminRequest(Builder builder) {
			userIdentifier = builder.userIdentifier;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public ByAdminRequest build() {
				return new ByAdminRequest(this);
			}
		}
	}
}
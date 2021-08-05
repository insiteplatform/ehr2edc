package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.ehr4cr.workbench.local.security.annotation.HasUserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface UpdateSecurityQuestion {
	void updateSecurityQuestion(@Valid Request request);

	final class Request implements HasUserIdentifier {
		@NotNull
		private final UserIdentifier userIdentifier;
		@NotBlank
		private final String questionId;
		@NotBlank
		private final String questionAnswer;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
			questionId = builder.questionId;
			questionAnswer = builder.questionAnswer;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@Override
		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public String getQuestionId() {
			return questionId;
		}

		public String getQuestionAnswer() {
			return questionAnswer;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private String questionId;
			private String questionAnswer;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withQuestionId(String questionId) {
				this.questionId = questionId;
				return this;
			}

			public Builder withQuestionAnswer(String questionAnswer) {
				this.questionAnswer = questionAnswer;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

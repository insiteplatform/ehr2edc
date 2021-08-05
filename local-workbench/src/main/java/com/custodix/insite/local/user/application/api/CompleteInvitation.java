package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;

import com.custodix.insite.local.user.vocabulary.Password;

public interface CompleteInvitation {
	void completeInvitation(@Valid Request request);

	final class Request {
		private final long userId;
		@Valid
		private final Password password;
		private final String tempPassword;
		private final String securityQuestionId;
		private final String securityQuestionAnswer;

		private Request(Builder builder) {
			userId = builder.userId;
			password = builder.password;
			tempPassword = builder.tempPassword;
			securityQuestionId = builder.securityQuestionId;
			securityQuestionAnswer = builder.securityQuestionAnswer;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public Password getPassword() {
			return password;
		}

		public String getTempPassword() {
			return tempPassword;
		}

		public String getSecurityQuestionId() {
			return securityQuestionId;
		}

		public String getSecurityQuestionAnswer() {
			return securityQuestionAnswer;
		}

		public static final class Builder {
			private long userId;
			private Password password;
			private String tempPassword;
			private String securityQuestionId;
			private String securityQuestionAnswer;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Builder withPassword(Password password) {
				this.password = password;
				return this;
			}

			public Builder withTempPassword(String tempPassword) {
				this.tempPassword = tempPassword;
				return this;
			}

			public Builder withSecurityQuestionId(String securityQuestionId) {
				this.securityQuestionId = securityQuestionId;
				return this;
			}

			public Builder withSecurityQuestionAnswer(String securityQuestionAnswer) {
				this.securityQuestionAnswer = securityQuestionAnswer;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

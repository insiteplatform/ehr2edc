package com.custodix.insite.local.user;

import java.util.List;
import java.util.Locale;

import eu.ehr4cr.workbench.local.model.security.Question;

public interface GetUserForActivation {
	Response getUser(Request request);

	final class Request {
		private final long userId;
		private final String tempPassword;
		private final Locale locale;

		private Request(Builder builder) {
			userId = builder.userId;
			tempPassword = builder.tempPassword;
			locale = builder.locale;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public String getTempPassword() {
			return tempPassword;
		}

		public Locale getLocale() {
			return locale;
		}

		public static final class Builder {
			private long userId;
			private String tempPassword;
			private Locale locale;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Builder withTempPassword(String tempPassword) {
				this.tempPassword = tempPassword;
				return this;
			}

			public Builder withLocale(Locale locale) {
				this.locale = locale;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final long userId;
		private final String userEmail;
		private final boolean userExpired;
		private final List<Question> securityQuestions;

		private Response(Builder builder) {
			userId = builder.userId;
			userEmail = builder.userEmail;
			userExpired = builder.userExpired;
			securityQuestions = builder.securityQuestions;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public boolean isUserExpired() {
			return userExpired;
		}

		public List<Question> getSecurityQuestions() {
			return securityQuestions;
		}

		public static final class Builder {
			private long userId;
			private String userEmail;
			private boolean userExpired;
			private List<Question> securityQuestions;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Builder withUserEmail(String userEmail) {
				this.userEmail = userEmail;
				return this;
			}

			public Builder withUserExpired(boolean userExpired) {
				this.userExpired = userExpired;
				return this;
			}

			public Builder withSecurityQuestions(List<Question> securityQuestions) {
				this.securityQuestions = securityQuestions;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class InvitationAlreadyCompletedException extends RuntimeException {

	}

	final class InvitationCompletionInvalidException extends RuntimeException {

	}
}

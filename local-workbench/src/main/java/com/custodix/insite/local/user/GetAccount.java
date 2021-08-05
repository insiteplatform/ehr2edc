package com.custodix.insite.local.user;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import eu.ehr4cr.workbench.local.model.security.Question;
import eu.ehr4cr.workbench.local.security.annotation.HasUserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface GetAccount {
	Response getAccount(Request request);

	final class Request implements HasUserIdentifier {
		private final UserIdentifier userIdentifier;
		private final Locale locale;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
			locale = builder.locale;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@Override
		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public Locale getLocale() {
			return locale;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private Locale locale;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
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
		private final String userEmail;
		private final String userName;
		private final Date passwordExpiryDate;
		private final String securityQuestionId;
		private final String securityQuestionAnswer;
		private final List<Question> questions;
		private final TreatingPhysicianInfo treatingPhysician;

		private Response(Builder builder) {
			userEmail = builder.userEmail;
			userName = builder.userName;
			passwordExpiryDate = builder.passwordExpiryDate;
			securityQuestionId = builder.securityQuestionId;
			securityQuestionAnswer = builder.securityQuestionAnswer;
			questions = builder.questions;
			treatingPhysician = builder.treatingPhysician;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public String getUserEmail() {
			return userEmail;
		}

		public String getUserName() {
			return userName;
		}

		public Optional<Date> getPasswordExpiryDate() {
			return Optional.ofNullable(passwordExpiryDate);
		}

		public String getSecurityQuestionId() {
			return securityQuestionId;
		}

		public String getSecurityQuestionAnswer() {
			return securityQuestionAnswer;
		}

		public List<Question> getQuestions() {
			return questions;
		}

		public Optional<TreatingPhysicianInfo> getTreatingPhysician() {
			return Optional.ofNullable(treatingPhysician);
		}

		public static final class Builder {
			private String userEmail;
			private String userName;
			private Date passwordExpiryDate;
			private String securityQuestionId;
			private String securityQuestionAnswer;
			private List<Question> questions;
			private TreatingPhysicianInfo treatingPhysician;

			private Builder() {
			}

			public Builder withUserEmail(String userEmail) {
				this.userEmail = userEmail;
				return this;
			}

			public Builder withUserName(String userName) {
				this.userName = userName;
				return this;
			}

			public Builder withPasswordExpiryDate(Date passwordExpiryDate) {
				this.passwordExpiryDate = passwordExpiryDate;
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

			public Builder withQuestions(List<Question> questions) {
				this.questions = questions;
				return this;
			}

			public Builder withTreatingPhysician(TreatingPhysicianInfo treatingPhysician) {
				this.treatingPhysician = treatingPhysician;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}

		public static final class TreatingPhysicianInfo {
			private final String providerId;
			private final boolean defaultPhysician;

			TreatingPhysicianInfo(String providerId, boolean defaultPhysician) {
				this.providerId = providerId;
				this.defaultPhysician = defaultPhysician;
			}

			public String getProviderId() {
				return providerId;
			}

			public boolean isDefaultPhysician() {
				return defaultPhysician;
			}
		}
	}
}

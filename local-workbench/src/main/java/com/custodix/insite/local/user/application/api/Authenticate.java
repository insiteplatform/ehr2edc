package com.custodix.insite.local.user.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.user.vocabulary.AuthenticateResult;
import com.custodix.insite.local.user.vocabulary.Email;
import com.custodix.insite.local.user.vocabulary.Password;
import com.custodix.insite.local.user.vocabulary.validation.Message;

import eu.ehr4cr.workbench.local.model.security.User;

public interface Authenticate {
	Response authenticate(@Valid Request request);

	final class Request {
		@NotNull
		@Valid
		private final Email email;
		@NotNull
		private final Password password;

		private Request(Builder builder) {
			email = builder.email;
			password = builder.password;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Email getEmail() {
			return email;
		}

		public Password getPassword() {
			return password;
		}

		public static final class Builder {
			private Email email;
			private Password password;

			private Builder() {
			}

			public Builder withEmail(Email email) {
				this.email = email;
				return this;
			}

			public Builder withPassword(Password password) {
				this.password = password;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final AuthenticateResult result;
		private final Message message;
		private final User user;

		private Response(Builder builder) {
			result = builder.result;
			message = builder.message;
			user = builder.user;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public AuthenticateResult getResult() {
			return result;
		}

		public Message getMessage() {
			return message;
		}

		public User getUser() {
			return user;
		}

		public static final class Builder {
			private AuthenticateResult result;
			private Message message;
			private User user;

			private Builder() {
			}

			public Builder withResult(AuthenticateResult result) {
				this.result = result;
				return this;
			}

			public Builder withMessage(Message message) {
				this.message = message;
				return this;
			}

			public Builder withUser(User user) {
				this.user = user;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

}

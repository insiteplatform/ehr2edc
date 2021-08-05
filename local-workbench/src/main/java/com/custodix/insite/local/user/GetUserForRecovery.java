package com.custodix.insite.local.user;

public interface GetUserForRecovery {
	Response getUser(Request request);

	final class Request {
		private final long userId;
		private final String tempPassword;

		private Request(Builder builder) {
			userId = builder.userId;
			tempPassword = builder.tempPassword;
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

		public static final class Builder {
			private long userId;
			private String tempPassword;

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

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final long userId;
		private final String userEmail;
		private final boolean userExpired;

		private Response(Builder builder) {
			userId = builder.userId;
			userEmail = builder.userEmail;
			userExpired = builder.userExpired;
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

		public static final class Builder {
			private long userId;
			private String userEmail;
			private boolean userExpired;

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

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class RecoveryAlreadyCompletedException extends RuntimeException {

	}

	final class RecoveryCompletionInvalidException extends RuntimeException {

	}
}

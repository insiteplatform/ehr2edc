package eu.ehr4cr.workbench.local.usecases.user.register;

public interface RegisterUser {
	void register(Request request);

	final class Request {
		private final String userEmail;
		private final String userName;

		private Request(Builder builder) {
			userEmail = builder.userEmail;
			userName = builder.userName;
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

		public static final class Builder {
			private String userEmail;
			private String userName;

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

			public Request build() {
				return new Request(this);
			}
		}
	}

}

package com.custodix.insite.local.user;

public interface ReinviteUser {
	void reinvite(Request request);

	final class Request {
		private final long userId;

		private Request(Builder builder) {
			userId = builder.userId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public long getUserId() {
			return userId;
		}

		public static final class Builder {
			private long userId;

			private Builder() {
			}

			public Builder withUserId(long userId) {
				this.userId = userId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}

package com.custodix.insite.local.user;

import java.util.List;

public interface GetAdministrators {
	Response getAdministrators();

	final class Response {
		private final List<String> emails;

		private Response(Builder builder) {
			emails = builder.emails;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public List<String> getEmails() {
			return emails;
		}

		public static final class Builder {
			private List<String> emails;

			private Builder() {
			}

			public Builder withEmails(List<String> emails) {
				this.emails = emails;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}

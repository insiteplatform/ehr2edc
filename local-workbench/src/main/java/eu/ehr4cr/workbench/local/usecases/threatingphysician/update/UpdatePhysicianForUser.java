package eu.ehr4cr.workbench.local.usecases.threatingphysician.update;

import eu.ehr4cr.workbench.local.security.annotation.HasUserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface UpdatePhysicianForUser {
	void update(Request request);

	final class Request implements HasUserIdentifier {
		private final UserIdentifier userIdentifier;
		private final String providerId;
		private final boolean defaultAssignee;

		private Request(Builder builder) {
			userIdentifier = builder.userIdentifier;
			providerId = builder.providerId;
			defaultAssignee = builder.defaultAssignee;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@Override
		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public String getProviderId() {
			return providerId;
		}

		public boolean isDefaultAssignee() {
			return defaultAssignee;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private String providerId;
			private boolean defaultAssignee;

			private Builder() {
			}

			public Builder withUserIdentifier(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withProviderId(String providerId) {
				this.providerId = providerId;
				return this;
			}

			public Builder withDefaultAssignee(boolean defaultAssignee) {
				this.defaultAssignee = defaultAssignee;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
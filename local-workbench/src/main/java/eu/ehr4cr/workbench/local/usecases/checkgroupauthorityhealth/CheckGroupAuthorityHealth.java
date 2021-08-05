package eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth;

import java.util.List;

import eu.ehr4cr.workbench.local.global.AuthorityType;

public interface CheckGroupAuthorityHealth {

	Response checkHealth();

	final class Response {
		private final boolean healthy;
		private final List<GroupHealth> groupHealthInfo;
		private final List<AuthorityHealth> authorityHealthInfo;

		private Response(Builder builder) {
			healthy = builder.healthy;
			groupHealthInfo = builder.groupHealthInfo;
			authorityHealthInfo = builder.authorityHealthInfo;
		}

		public boolean isHealthy() {
			return healthy;
		}

		public List<GroupHealth> getGroupHealthInfo() {
			return groupHealthInfo;
		}

		public List<AuthorityHealth> getAuthorityHealthInfo() {
			return authorityHealthInfo;
		}

		static Builder builder() {
			return new Builder();
		}

		static final class Builder {
			private boolean healthy;
			private List<GroupHealth> groupHealthInfo;
			private List<AuthorityHealth> authorityHealthInfo;

			public Builder withHealthy(boolean healthy) {
				this.healthy = healthy;
				return this;
			}

			public Builder withGroupHealthInfo(List<GroupHealth> groupHealthInfo) {
				this.groupHealthInfo = groupHealthInfo;
				return this;
			}

			public Builder withAuthorityHealthInfo(List<AuthorityHealth> authorityHealthInfo) {
				this.authorityHealthInfo = authorityHealthInfo;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}

		public static final class GroupHealth {
			private final boolean healthy;
			private final boolean presentInDatabase;
			private final String name;
			private final List<AuthorityType> requiredAuthorities;
			private final List<AuthorityType> forbiddenAuthorities;

			private GroupHealth(GroupHealthBuilder builder) {
				healthy = builder.healthy;
				presentInDatabase = builder.presentInDatabase;
				name = builder.name;
				requiredAuthorities = builder.requiredAuthorities;
				forbiddenAuthorities = builder.forbiddenAuthorities;
			}

			public boolean isHealthy() {
				return healthy;
			}

			public boolean isPresentInDatabase() {
				return presentInDatabase;
			}

			public String getName() {
				return name;
			}

			public List<AuthorityType> getRequiredAuthorities() {
				return requiredAuthorities;
			}

			public List<AuthorityType> getForbiddenAuthorities() {
				return forbiddenAuthorities;
			}

			public boolean hasRequiredAuthorities() {
				return !requiredAuthorities.isEmpty();
			}

			public boolean hasForbiddenAuthorities() {
				return !forbiddenAuthorities.isEmpty();
			}

			public static GroupHealthBuilder builder() {
				return new GroupHealthBuilder();
			}

		}

		static final class GroupHealthBuilder {
			private boolean healthy;
			private boolean presentInDatabase;
			private String name;
			private List<AuthorityType> requiredAuthorities;
			private List<AuthorityType> forbiddenAuthorities;

			public GroupHealthBuilder withHealthy(boolean healthy) {
				this.healthy = healthy;
				return this;
			}

			public GroupHealthBuilder withPresentInDatabase(boolean presentInDatabase) {
				this.presentInDatabase = presentInDatabase;
				return this;
			}

			public GroupHealthBuilder withName(String name) {
				this.name = name;
				return this;
			}

			public GroupHealthBuilder withRequiredAuthorities(List<AuthorityType> missingAuthorities) {
				this.requiredAuthorities = missingAuthorities;
				return this;
			}

			public GroupHealthBuilder withForbiddenAuthorities(List<AuthorityType> undesiredAuthorities) {
				this.forbiddenAuthorities = undesiredAuthorities;
				return this;
			}

			public GroupHealth build() {
				return new GroupHealth(this);
			}
		}

		public static final class AuthorityHealth {
			private final boolean healthy;
			private final String name;

			private AuthorityHealth(AuthorityHealthBuilder builder) {
				healthy = builder.healthy;
				name = builder.name;
			}

			public boolean isHealthy() {
				return healthy;
			}

			public String getName() {
				return name;
			}

			@Override
			public String toString() {
				return "AuthorityHealth{" + "healthy=" + healthy + ", name='" + name + '\'' + '}';
			}

			public static AuthorityHealthBuilder builder() {
				return new AuthorityHealthBuilder();
			}
		}

		static final class AuthorityHealthBuilder {
			private boolean healthy;
			private String name;

			public AuthorityHealthBuilder withHealthy(boolean healthy) {
				this.healthy = healthy;
				return this;
			}

			public AuthorityHealthBuilder withName(String name) {
				this.name = name;
				return this;
			}

			public AuthorityHealth build() {
				return new AuthorityHealth(this);
			}
		}
	}
}
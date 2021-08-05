package eu.ehr4cr.workbench.local.usecases.checkflywayhealth;

import java.util.List;

public interface CheckFlywayHealth {

	Response check();

	final class Response {
		private final boolean healthy;
		private final String currentVersion;
		private final List<String> failedVersions;
		private final List<String> pendingVersions;

		private Response(Builder builder) {
			healthy = builder.healthy;
			currentVersion = builder.currentVersion;
			failedVersions = builder.failedVersions;
			pendingVersions = builder.pendingVersions;
		}

		public boolean isHealthy() {
			return healthy;
		}

		public String getCurrentVersion() {
			return currentVersion;
		}

		public List<String> getFailedVersions() {
			return failedVersions;
		}

		public List<String> getPendingVersions() {
			return pendingVersions;
		}

		public static final Builder builder() {
			return new Builder();
		}

		public static final class Builder {
			private boolean healthy;
			private String currentVersion;
			private List<String> failedVersions;
			private List<String> pendingVersions;

			public Builder withHealthy(boolean healthy) {
				this.healthy = healthy;
				return this;
			}

			public Builder withCurrentVersion(String currentVersion) {
				this.currentVersion = currentVersion;
				return this;
			}

			public Builder withFailedVersions(List<String> failedVersions) {
				this.failedVersions = failedVersions;
				return this;
			}

			public Builder withPendingVersions(List<String> pendingVersions) {
				this.pendingVersions = pendingVersions;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
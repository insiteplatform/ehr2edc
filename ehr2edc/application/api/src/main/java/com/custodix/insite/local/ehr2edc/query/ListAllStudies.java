package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.util.List;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;

public interface ListAllStudies {

	@Allow(AUTHENTICATED)
	Response allStudies();

	final class Response {
		private final List<Study> studies;

		private Response(Builder builder) {
			studies = builder.studies;
		}

		public List<Study> getStudies() {
			return studies;
		}

		public static Builder newBuilder(List<Study> studies) {
			return new Builder(studies);
		}

		public static final class Builder {
			private final List<Study> studies;

			private Builder(List<Study> studies) {
				this.studies = studies;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Study {
		private final String id;
		private final String name;
		private final String description;

		private Study(Builder builder) {
			id = builder.id;
			name = builder.name;
			description = builder.description;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private String id;
			private String name;
			private String description;

			private Builder() {
			}

			public Builder withId(String id) {
				this.id = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withDescription(String description) {
				this.description = description;
				return this;
			}

			public Study build() {
				return new Study(this);
			}
		}
	}

}

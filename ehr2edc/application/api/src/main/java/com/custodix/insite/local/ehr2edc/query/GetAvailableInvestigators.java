package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface GetAvailableInvestigators {

	@Allow(AUTHENTICATED)
	Response get(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final StudyId studyId;

		private Request(Builder builder) {
			studyId = builder.studyId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			return builder;
		}

		public static final class Builder {
			private StudyId studyId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<PotentialInvestigator> potentialInvestigators;

		private Response(Builder builder) {
			potentialInvestigators = builder.potentialInvestigators;
		}

		public List<PotentialInvestigator> getPotentialInvestigators() {
			return potentialInvestigators;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.potentialInvestigators = copy.getPotentialInvestigators();
			return builder;
		}

		public static final class Builder {
			private List<PotentialInvestigator> potentialInvestigators;

			private Builder() {
			}

			public Builder withPotentialInvestigators(List<PotentialInvestigator> potentialInvestigators) {
				this.potentialInvestigators = potentialInvestigators;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class PotentialInvestigator {
		private final UserIdentifier userIdentifier;
		private final String name;

		private PotentialInvestigator(Builder builder) {
			userIdentifier = builder.userIdentifier;
			name = builder.name;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(PotentialInvestigator copy) {
			Builder builder = new Builder();
			builder.userIdentifier = copy.getUserIdentifier();
			builder.name = copy.getName();
			return builder;
		}

		public UserIdentifier getUserIdentifier() {
			return userIdentifier;
		}

		public String getName() {
			return name;
		}

		public static final class Builder {
			private UserIdentifier userIdentifier;
			private String name;

			private Builder() {
			}

			public Builder withUserId(UserIdentifier userIdentifier) {
				this.userIdentifier = userIdentifier;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public PotentialInvestigator build() {
				return new PotentialInvestigator(this);
			}
		}
	}
}
package com.custodix.insite.local.ehr2edc.rest.models.outcome;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = OperationOutcome.Builder.class)
@JsonPropertyOrder({ "issues" })
public final class OperationOutcome {
	@NotNull
	private final Collection<Issue> issues;

	private OperationOutcome(final Builder builder) {
		issues = builder.issues;
	}

	public static OperationOutcome of(Collection<Issue> issues) {
		return newBuilder().withIssues(issues)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Collection<Issue> getIssues() {
		return issues;
	}

	public static final class Builder {
		private Collection<Issue> issues;

		private Builder() {
		}

		public Builder withIssues(final Collection<Issue> issues) {
			this.issues = issues;
			return this;
		}

		public OperationOutcome build() {
			return new OperationOutcome(this);
		}
	}
}

package eu.ehr4cr.workbench.local.model.patient;

import java.util.List;

public final class Observation {
	private final String name;
	private final List<Fact> facts;

	private Observation(Builder builder) {
		name = builder.name;
		facts = builder.facts;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public List<Fact> getFacts() {
		return facts;
	}

	public static final class Builder {
		private String name;
		private List<Fact> facts;

		private Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withFacts(List<Fact> facts) {
			this.facts = facts;
			return this;
		}

		public Observation build() {
			return new Observation(this);
		}
	}
}

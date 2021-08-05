package com.custodix.insite.local.ehr2edc;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.custodix.insite.local.ehr2edc.query.InvestigatorProjection;
import com.custodix.insite.local.ehr2edc.query.InvestigatorsProjection;
import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

final class Investigators implements InvestigatorsProjection {
	private final Collection<Investigator> assigned;

	private Investigators(Collection<Investigator> investigators) {
		this.assigned = investigators;
	}

	private Investigators(Builder builder) {
		assigned = builder.investigators;
	}

	public Collection<InvestigatorSnapshot> toSnapshot() {
		return assigned.stream()
				.map(Investigator::toSnapshot)
				.collect(toList());
	}

	static Investigators restoreFrom(Collection<InvestigatorSnapshot> snapshots) {
		return snapshots.stream()
				.map(Investigator::restoreFromSnapshot)
				.collect(collectingAndThen(toList(), Investigators::new));
	}

	@Override
	public Collection<InvestigatorProjection> getAssigned() {
		return Collections.unmodifiableList(new ArrayList<>(assigned));
	}

	void assign(UserIdentifier investigatorId, String name) {
		if (doesNotContain(investigatorId)) {
			this.assigned.add(Investigator.create(investigatorId, name));
		}
	}

	boolean doesNotContain(UserIdentifier investigatorId) {
		return assigned.stream()
				.noneMatch(i -> investigatorId.equals(i.getInvestigatorId()));
	}

	void unassign(UserIdentifier investigatorId) {
		assigned.removeIf(investigator -> investigatorId.equals(investigator.getInvestigatorId()));
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private Collection<Investigator> investigators = new ArrayList<>();

		private Builder() {
		}

		public Builder withInvestigators(Collection<Investigator> investigators) {
			this.investigators = investigators;
			return this;
		}

		public Investigators build() {
			return new Investigators(this);
		}
	}
}

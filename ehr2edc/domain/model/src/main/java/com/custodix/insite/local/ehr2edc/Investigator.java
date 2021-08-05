package com.custodix.insite.local.ehr2edc;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.InvestigatorProjection;
import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

final class Investigator implements InvestigatorProjection {

	private final UserIdentifier investigatorId;
	private final String name;

	private Investigator(UserIdentifier investigatorId, String name) {
		this.investigatorId = investigatorId;
		this.name = name;
	}

	public static Investigator create(UserIdentifier investigatorId, String name) {
		return new Investigator(investigatorId, name);
	}

	InvestigatorSnapshot toSnapshot() {
		return InvestigatorSnapshot.newBuilder()
				.withUserId(investigatorId)
				.withName(name)
				.build();
	}

	static Investigator restoreFromSnapshot(InvestigatorSnapshot snapshot) {
		return new Investigator(snapshot.getUserId(), snapshot.getName());
	}

	@Override
	public UserIdentifier getInvestigatorId() {
		return investigatorId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Investigator that = (Investigator) o;
		return Objects.equals(investigatorId, that.investigatorId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(investigatorId);
	}
}

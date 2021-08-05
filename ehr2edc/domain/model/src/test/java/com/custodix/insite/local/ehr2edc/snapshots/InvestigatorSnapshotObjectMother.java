package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.HashSet;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public class InvestigatorSnapshotObjectMother {

	public static InvestigatorSnapshot aDefaultInvestigatorSnapshot() {
		return aDefaultInvestigatorSnapshotBuilder().build();
	}

	public static InvestigatorSnapshot.Builder aDefaultInvestigatorSnapshotBuilder() {
		return InvestigatorSnapshot.newBuilder()
				.withName("investigator-name123")
				.withUserId(UserIdentifier.of("user-identifier-123"));
	}

	public static Set<InvestigatorSnapshot> generateInvestigatorFromUserIdentifier(UserIdentifier... ids) {
		Set<InvestigatorSnapshot> investigators = new HashSet<>();
		for(UserIdentifier id : ids) {
			investigators.add(InvestigatorSnapshot.newBuilder()
					.withUserId(id)
					.withName("Known investigator")
					.build());
		}
		return investigators;
	}
}
package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public final class InvestigatorDocument {
	private final String userId;
	private final String name;

	@PersistenceConstructor
	private InvestigatorDocument(String userId, String name) {
		this.userId = userId;
		this.name = name;
	}

	static Collection<InvestigatorDocument> fromSnapshots(Collection<InvestigatorSnapshot> investigators) {
		return investigators.stream()
				.map(s -> new InvestigatorDocument(s.getUserId()
						.getId(), s.getName()))
				.collect(Collectors.toList());
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public InvestigatorSnapshot toSnapshot() {
		return InvestigatorSnapshot.newBuilder()
				.withUserId(UserIdentifier.of(userId))
				.withName(name)
				.build();
	}
}

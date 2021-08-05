package com.custodix.insite.local.user.infra.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePhysicianRequest {
	private final String providerId;
	private final boolean defaultAssignee;

	@JsonCreator
	public UpdatePhysicianRequest(@JsonProperty("providerId") String providerId, @JsonProperty("isDefault") boolean defaultAssignee) {
		this.providerId = providerId;
		this.defaultAssignee = defaultAssignee;
	}

	public String getProviderId() {
		return providerId;
	}

	public boolean isDefaultAssignee() {
		return defaultAssignee;
	}
}

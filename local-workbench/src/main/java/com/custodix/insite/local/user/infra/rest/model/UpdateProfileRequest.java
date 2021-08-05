package com.custodix.insite.local.user.infra.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProfileRequest {
	private final String username;
	private final String email;

	@JsonCreator
	public UpdateProfileRequest(@JsonProperty("username") String username, @JsonProperty("email") String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}
}

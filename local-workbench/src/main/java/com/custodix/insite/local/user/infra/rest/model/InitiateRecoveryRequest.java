package com.custodix.insite.local.user.infra.rest.model;

import java.util.Optional;

import com.custodix.insite.local.user.vocabulary.Email;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InitiateRecoveryRequest {
	private final String userMail;
	private final String userAnswer;

	@JsonCreator
	public InitiateRecoveryRequest(@JsonProperty("userMail") String userMail, @JsonProperty("userAnswer") String userAnswer) {
		this.userMail = userMail;
		this.userAnswer = userAnswer;
	}

	public Email getUserMail() {
		return Email.of(userMail);
	}

	public Optional<String> getUserAnswer() {
		return Optional.ofNullable(userAnswer);
	}
}

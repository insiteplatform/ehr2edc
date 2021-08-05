package com.custodix.insite.local.ehr2edc.ehr.epic.vocabulary;

import javax.validation.constraints.NotBlank;

public class OAuthAccessToken {
	@NotBlank
	private String value;

	private OAuthAccessToken(String value) {
		this.value = value;
	}

	public static OAuthAccessToken of(String value) {
		return new OAuthAccessToken(value);
	}

	public String getValue() {
		return value;
	}
}

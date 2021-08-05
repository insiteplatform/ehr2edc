package com.custodix.insite.local.user.vocabulary.validation;

public class Message {
	private final String key;
	private final Object[] parameters;

	public Message(String key, Object... parameters) {
		this.key = key;
		this.parameters = parameters;
	}

	public String getKey() {
		return key;
	}

	public Object[] getParameters() {
		return parameters;
	}
}

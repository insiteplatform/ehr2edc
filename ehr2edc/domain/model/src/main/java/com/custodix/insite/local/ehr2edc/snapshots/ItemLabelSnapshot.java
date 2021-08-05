package com.custodix.insite.local.ehr2edc.snapshots;

public final class ItemLabelSnapshot {
	private String name;
	private QuestionSnapshot question;

	public ItemLabelSnapshot(String name) {
		this(name, null);
	}

	public ItemLabelSnapshot(String name, QuestionSnapshot question) {
		this.name = name;
		this.question = question;
	}

	public String getName() {
		return name;
	}

	public QuestionSnapshot getQuestion() {
		return question;
	}
}

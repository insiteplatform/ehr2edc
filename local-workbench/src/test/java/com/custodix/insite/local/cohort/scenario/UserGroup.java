package com.custodix.insite.local.cohort.scenario;

public enum UserGroup {
	ADMINISTRATOR("Administrators"), DRM("Data Relationship Managers"), REGULAR_USER("Users"),
	POWER_USER("Power Users");

	private final String groupName;

	UserGroup(final String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
}

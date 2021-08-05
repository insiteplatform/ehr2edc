package eu.ehr4cr.workbench.local.model.security;

import eu.ehr4cr.workbench.local.global.GroupType;

public enum UserRole {
	ADM(GroupType.ADM), DRM(GroupType.DRM), PWR(GroupType.PWR);

	private final GroupType groupType;

	UserRole(GroupType groupType) {
		this.groupType = groupType;
	}

	public static UserRole fromGroupType(GroupType groupType) {
		return UserRole.valueOf(groupType.name());
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public String getLabel() {
		return groupType.getInnerName();
	}

}

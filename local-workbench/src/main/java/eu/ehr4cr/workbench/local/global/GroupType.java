package eu.ehr4cr.workbench.local.global;

import java.util.Arrays;

public enum GroupType {
	DRM("Data Relationship Managers"), ADM("Administrators"), USR("Users"), PWR("Power Users");

	private final String innerName;

	GroupType(String innerName) {
		this.innerName = innerName;
	}

	public static GroupType fromInnerName(String innerName) {
		return Arrays.stream(values())
				.filter(g -> g.innerName.equalsIgnoreCase(innerName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No group type with name " + innerName));
	}

	public String getInnerName() {
		return innerName;
	}
}

package eu.ehr4cr.workbench.local.usecases.user;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;

public class RoleInfo {
	private final long id;
	private final GroupType type;

	public RoleInfo(Group group) {
		this.id = group.getId();
		this.type = group.getType();
	}

	public long getId() {
		return id;
	}

	public GroupType getType() {
		return type;
	}
}

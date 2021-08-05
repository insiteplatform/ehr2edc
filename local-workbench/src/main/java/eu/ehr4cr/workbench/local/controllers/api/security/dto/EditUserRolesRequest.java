package eu.ehr4cr.workbench.local.controllers.api.security.dto;

import java.util.List;

import eu.ehr4cr.workbench.local.model.security.UserRole;

public class EditUserRolesRequest {
	private long userId;
	private List<UserRole> roles;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
}

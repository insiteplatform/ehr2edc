package eu.ehr4cr.workbench.local.controllers.api.security.dto;

import java.util.List;

import eu.ehr4cr.workbench.local.model.security.UserRole;

public class InviteUserRequest {
	private String username;
	private String mail;
	private List<UserRole> roles;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
}

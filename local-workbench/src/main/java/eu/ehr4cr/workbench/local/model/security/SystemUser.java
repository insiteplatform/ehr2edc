package eu.ehr4cr.workbench.local.model.security;

import java.util.Collections;
import java.util.Set;

import eu.ehr4cr.workbench.local.global.AuthorityType;

public class SystemUser extends User {
	public static final Long ID = -1L;
	private static final String SYSTEM = "system";

	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getUsername() {
		return SYSTEM;
	}

	@Override
	public boolean hasAuthority(AuthorityType authority) {
		return true;
	}

	@Override
	public Set<Authority> getAuthorities() {
		return Collections.emptySet();
	}

	@Override
	public Set<Group> getGroups() {
		return Collections.emptySet();
	}

	@Override
	public String getFirstName() {
		return SYSTEM;
	}

	@Override
	public String getLastName() {
		return SYSTEM;
	}

	@Override
	public String getEmail() {
		return SYSTEM;
	}
}

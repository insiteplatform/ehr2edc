package eu.ehr4cr.workbench.local.model.security;

import java.util.Collections;
import java.util.Set;

public class AnonymousUser extends User {

	private static final String ANONYMOUS = "anonymous";

	@Override
	public String getUsername() {
		return ANONYMOUS;
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
		return ANONYMOUS;
	}

	@Override
	public String getLastName() {
		return ANONYMOUS;
	}

	@Override
	public String getEmail() {
		return ANONYMOUS;
	}

	@Override
	public boolean isPasswordImminentlyExpiring() {
		return false;
	}
}

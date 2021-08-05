package eu.ehr4cr.workbench.local.security;

import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class SecurityContextUser extends User implements OAuth2User {

	private static final long serialVersionUID = -3531439484732724601L;

	private final eu.ehr4cr.workbench.local.model.security.User workbenchUser;

	public SecurityContextUser(eu.ehr4cr.workbench.local.model.security.User workbenchUser) {
		super(workbenchUser.getUsername(), "", new ArrayList<>());
		this.workbenchUser = workbenchUser;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return emptyMap();
	}

	@Override
	public String getName() {
		return workbenchUser.getUsername();
	}

	public eu.ehr4cr.workbench.local.model.security.User getWorkbenchUser() {
		return workbenchUser;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		final SecurityContextUser that = (SecurityContextUser) o;
		return Objects.equals(workbenchUser, that.workbenchUser);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), workbenchUser);
	}
}
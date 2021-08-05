package eu.ehr4cr.workbench.local;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

public class DummySecurityContext {

	public static final String USERNAME = "test";
	public static final String PASSWORD = "testP";
	public static final long ID = 1;

	private DummySecurityContext() {
	}

	public static User initDummySecurityContext() {
		final User user = createUser();
		initDummySecurityContext(user);
		return user;
	}

	public static void initDummySecurityContext(User user) {
		final SecurityContextUser securityContextUser = new SecurityContextUser(user);
		SecurityContextHolder.getContext()
				.setAuthentication(createToken(securityContextUser));
	}

	private static User createUser() {
		final User user = new User(USERNAME, PASSWORD);
		user.setId(ID);
		return user;
	}

	public static User getUser() {
		final Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		final SecurityContextUser securityContextUser = (SecurityContextUser) authentication.getPrincipal();
		return securityContextUser.getWorkbenchUser();
	}

	private static UsernamePasswordAuthenticationToken createToken(SecurityContextUser securityContextUser) {
		return new UsernamePasswordAuthenticationToken(securityContextUser, PASSWORD, new ArrayList<>());
	}
}

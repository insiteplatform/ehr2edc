package eu.ehr4cr.workbench.local.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.ehr4cr.workbench.local.model.security.AnonymousUser;
import eu.ehr4cr.workbench.local.model.security.User;

public class BaseControllerTest {
	private BaseController baseController = new BaseController() {
	};

	@Test
	public void getUser() throws Exception {
		SecurityContextHolder.clearContext();
		User user = baseController.getUser();
		assertThat(user).isInstanceOf(AnonymousUser.class);
		assertThat(user.getAuthorities()).isEmpty();
		assertThat(user.getGroups()).isEmpty();
		assertThat(user.isPasswordImminentlyExpiring()).isFalse();
	}

}

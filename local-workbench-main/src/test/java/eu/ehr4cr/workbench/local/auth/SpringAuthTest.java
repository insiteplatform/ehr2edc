package eu.ehr4cr.workbench.local.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import eu.ehr4cr.workbench.local.SpringAppTestConfig;
import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.global.IDatabaseInitiator;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.service.TestTimeService;

@Transactional
@RunWith(SpringRunner.class)
public class SpringAuthTest extends SpringAppTestConfig {

	@Autowired
	private IDatabaseInitiator initiator;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private IUserMgrService userService;

	private MockMvc mvc;

	@Before
	public void setup() {
		DomainTime.setTime(new TestTimeService());
		initiator.launch();
		registerTestUsers();
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	private void registerTestUsers() {
		if (userService.findUserByUsername("Test user") == null) {
			userService.createUser("test2@custodix.com", "Test user 2", "test2");
			User acceptedUser = userService.createUser("test@custodix.com", "Test user", "test");
			userService.acceptUser(acceptedUser.getId());

			User adminUser = userService.createUser("testAdmin@custodix.com", "Test admin", "testAdmin");

			userService.acceptUser(adminUser.getId());
			userService.assignUserToGroups(adminUser.getId(), Arrays.asList(GroupType.values()));

			User assignUser = userService.createUser("test.assign@custodix.com", "Test assign", "testassign");
			userService.acceptUser(assignUser.getId());

			userService.createUser("test.accept@custodix.com", "Test accept", "testaccept");
			userService.createUser("test.delete@custodix.com", "Test delete", "testdelete");
		}
	}

	@Test
	public void testLoginEmpty() throws Exception {
		mvc.perform(formLogin(WebRoutes.login).user("")
				.password(""))
				.andExpect(redirectedUrl(WebRoutes.login));
	}

	@Test
	public void testLoginInvalid() throws Exception {
		mvc.perform(formLogin(WebRoutes.login).user("email", "fail")
				.password("password", "fail"))
				.andExpect(redirectedUrl(WebRoutes.login));
		mvc.perform(get(WebRoutes.logout));
	}

	@Test
	public void testLoginAccepted() throws Exception {
		mvc.perform(formLogin(WebRoutes.login).user("email", "test@custodix.com")
				.password("password", "test"))
				.andReturn()
				.getResponse()
				.getRedirectedUrl();
		mvc.perform(post(WebRoutes.logout));
	}

	@Test
	public void testLoginCaseInsensitive() throws Exception {
		mvc.perform(formLogin(WebRoutes.login).user("email", "TEST@custodix.com")
				.password("password", "test"))
				.andReturn()
				.getResponse()
				.getRedirectedUrl();
	}

	@Test
	public void testAcceptNoPermissions() throws Exception {
		User normalUser = userService.createUser("test32@custodix.com", "Test user 32", "test32");
		assertThat(normalUser).isNotNull();
		mvc.perform(post(WebRoutes.acceptMember).param("userId", normalUser.getId()
				.toString())
				.session(login(normalUser))
				.with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testAcceptWithPermissionsAndValid() throws Exception {
		User adminUser = userService.findUserByUsername("Test admin");
		User normalUser = userService.findUserByUsername("Test user");

		assertThat(adminUser).isNotNull();
		assertThat(normalUser).isNotNull();

		mvc.perform(post(WebRoutes.acceptMember).session(login(adminUser))
				.param("userId", normalUser.getId()
						.toString())
				.with(csrf()))
				.andExpect(status().isOk());
		mvc.perform(get(WebRoutes.logout));
	}

	@Test
	public void testDeleteNoPermissions() throws Exception {
		User normalUser = userService.findUserByUsername("Test user 2");
		assertThat(normalUser).isNotNull();
		mvc.perform(post(WebRoutes.deleteMember).param("userId", normalUser.getId()
				.toString())
				.session(login(normalUser))
				.with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testDeleteWithPermissionsAndValid() throws Exception {
		User adminUser = userService.findUserByUsername("Test admin");
		User normalUser = userService.findUserByUsername("Test user");

		assertThat(adminUser).isNotNull();
		assertThat(normalUser).isNotNull();

		mvc.perform(post(WebRoutes.deleteMember).param("userId", normalUser.getId()
				.toString())
				.session(login(adminUser))
				.with(csrf()))
				.andExpect(status().isOk());
		mvc.perform(get(WebRoutes.logout));
	}

}

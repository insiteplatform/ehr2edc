package eu.ehr4cr.workbench.local.model.security;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.TestTimeService;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

	private User user;

	@Before
	public void setUp() {
		DomainTime.setTime(new TestTimeService());
		user = new User("username","password");
	}

	@Test
	public void isAdminUser() {
		user.addGroup(new Group("Administrators"));
		assertThat(user.isAdministratorUser()).isTrue();
	}

	@Test
	public void isNoAdminUser() {
		assertThat(user.isAdministratorUser()).isFalse();
	}
}

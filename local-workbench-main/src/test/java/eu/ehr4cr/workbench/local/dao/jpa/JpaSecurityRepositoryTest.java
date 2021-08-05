package eu.ehr4cr.workbench.local.dao.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.custodix.insite.local.shared.exceptions.NotFoundException;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.AbstractWorkbenchTest;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.UserObjectMother;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional("localTransactionManager")
public class JpaSecurityRepositoryTest extends AbstractWorkbenchTest {

	@Autowired
	private SecurityDao securityDao;

	private Group group;
	private User activeUser;
	private User disabledUser;
	private User deletedUser;

	@Before
	public void init() {
		UserPopulator userPopulator = new UserPopulator(securityDao);
		group = userPopulator.persistGroup(new Group("DRM"));
		activeUser = userPopulator.persistUserInGroup(UserObjectMother.createActiveUser(), group);
		disabledUser = userPopulator.persistUserInGroup(UserObjectMother.createDisabledUser(), group);
		deletedUser = userPopulator.persistUserInGroup(UserObjectMother.createDeletedUser(), group);
	}

	@Test
	public void getUserByEmailActive_active() {
		User user = securityDao.getActiveUserByEmail(Email.of(activeUser.getEmail()));
		assertThat(user).as("active user should be found")
				.isEqualTo(activeUser);
	}

	@Test(expected = NotFoundException.class)
	public void getUserByEmailActive_disabled() {
		User user = securityDao.getActiveUserByEmail(Email.of(disabledUser.getEmail()));
		assertThat(user).as("disabled user should not be found")
				.isNull();
	}

	@Test(expected = NotFoundException.class)
	public void getUserByEmailActive_deleted() {
		User user = securityDao.getActiveUserByEmail(Email.of(deletedUser.getEmail()));
		assertThat(user).as("deleted user should not be found")
				.isNull();
	}

	@Test
	public void findUsersByGroupname() {
		List<User> users = securityDao.findUsersByGroupname(group.getName());
		assertThat(users).as("active group users should be found")
				.hasSize(1);
	}

}

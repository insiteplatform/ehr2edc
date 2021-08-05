package eu.ehr4cr.workbench.local.global.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.AbstractWorkbenchTest;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.IDatabaseInitiator;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.model.security.UserStatus;

@Transactional("localTransactionManager")
public class DatabaseInitiatorTest extends AbstractWorkbenchTest {
	@Autowired
	private SecurityDao securityDao;
	@Autowired
	private IDatabaseInitiator dbInitiator;

	@Test
	public void test_administrator_created() {
		// When application starts the admin user is created if there are no admin users presents
		validateAdminUser();
	}

	@Test
	public void test_administrator_exists_already() {
		// When application starts the admin user is created if there are no admin users presents
		dbInitiator.launch();
		validateAdminUser();
	}

	private void validateAdminUser() {
		User user = securityDao.findUserByEmail(Email.of("admin@custodix.com"))
				.orElse(null);
		assertThat(user).isNotNull();
		assertThat(user.getUsername()).isEqualTo("admin");
		assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
	}

}

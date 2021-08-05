package eu.ehr4cr.workbench.local.global.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.global.IDatabaseInitiator;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Component
@Transactional("localTransactionManager")
class DatabaseInitiator implements IDatabaseInitiator {
	private static final String ADMIN_MAIL_PARAMETER = "adminMail";
	private static final String ADMIN_USERNAME_PARAMETER = "adminUsername";
	private static final String ADMIN_PASS_PARAMETER = "adminPassword";

	private static final String ADMINISTRATOR_MAIL_DEFAULT = "support@custodix.com";
	private static final String ADMINISTRATOR_USERNAME_DEFAULT = "Administrator";

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitiator.class);

	private final SecurityDao securityDao;
	private final IUserMgrService userMgrService;
	private final Environment environment;

	@Autowired
	DatabaseInitiator(final SecurityDao securityDao, final IUserMgrService userMgrService,
			final Environment environment) {
		this.securityDao = securityDao;
		this.userMgrService = userMgrService;
		this.environment = environment;
	}

	@Override
	public void launch() {
		checkAdministrator();
	}

	private void checkAdministrator() {
		List<User> admins = userMgrService.findUsersByGroupname(GroupType.ADM.getInnerName());
		if (admins.isEmpty()) {
			createDefaultAdministrator();
		} else if (admins.stream()
				.allMatch(User::isExpired)) {
			LOGGER.warn("All administrator users are expired");
		}
	}

	private void createDefaultAdministrator() {
		LOGGER.info("Recreating the admin user");
		User adminUser = createNewAdministrator();
		acceptAdministrator(adminUser);
	}

	private User createNewAdministrator() {
		String adminUsername = environment.getProperty(ADMIN_USERNAME_PARAMETER, ADMINISTRATOR_USERNAME_DEFAULT);
		String adminMail = environment.getProperty(ADMIN_MAIL_PARAMETER, ADMINISTRATOR_MAIL_DEFAULT);
		User user = new User(adminUsername, adminMail, getGroups());
		securityDao.persist(user);
		return user;
	}

	private HashSet<Group> getGroups() {
		Group userGroup = securityDao.findGroup(GroupType.USR);
		Group adminGroup = securityDao.findGroup(GroupType.ADM);
		return new HashSet<>(Arrays.asList(userGroup, adminGroup));
	}

	private void acceptAdministrator(User adminUser) {
		String adminPassword = environment.getProperty(ADMIN_PASS_PARAMETER);
		if (adminPassword != null)
			userMgrService.acceptUser(adminUser.getId(), adminPassword);
		else
			userMgrService.acceptUser(adminUser.getId());
	}
}
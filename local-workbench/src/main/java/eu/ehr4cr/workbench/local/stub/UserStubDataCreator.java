package eu.ehr4cr.workbench.local.stub;

import java.util.Collections;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Component
@ConditionalOnProperty(value = "user.mock.email")
class UserStubDataCreator implements StubDataCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserStubDataCreator.class);
	private final SecurityDao securityDao;
	private final IUserMgrService userMgrService;
	private final String mockEmail;
	private final String mockPassword;
	private final String mockUsername;

	public UserStubDataCreator(SecurityDao securityDao, final IUserMgrService userMgrService,
			@Value("${user.mock.email}") final String mockEmail,
			@Value("${user.mock.password}") final String mockPassword,
			@Value("${user.mock.username}") final String mockUsername) {
		this.securityDao = securityDao;
		this.userMgrService = userMgrService;
		this.mockEmail = mockEmail;
		this.mockPassword = mockPassword;
		this.mockUsername = mockUsername;
	}

	@Transactional
	@Override
	public void createStubData() {
		LOGGER.debug("Creating mock user {}", mockEmail);
		if (!userMgrService.findUserByEmail(mockEmail).isPresent()) {
			User user = new User(mockUsername, mockEmail, getGroups());
			securityDao.persist(user);
			userMgrService.acceptUser(user.getId(), mockPassword);
			user = securityDao.findUserById(user.getIdentifier());
			user.updateSecurityQuestion("1", "test");
			securityDao.save(user);
		}
	}

	private HashSet<Group> getGroups() {
		Group userGroup = securityDao.findGroup(GroupType.USR);
		return new HashSet<>(Collections.singletonList(userGroup));
	}
}

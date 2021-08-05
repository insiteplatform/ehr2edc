package eu.ehr4cr.workbench.local.dao.jpa;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;

class UserPopulator {
	private static long userId = 1L;
	private final SecurityDao securityDao;

	UserPopulator(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public User persistUser(User user) {
		user.setId(userId);
		user.setUsername(user.getUsername() + userId);
		user.setEmail(user.getEmail() + userId);
		userId++;
		securityDao.persist(user);
		return user;
	}

	public User persistUserInGroup(User user, Group group) {
		user.addGroup(group);
		return persistUser(user);
	}

	public Group persistGroup(Group group) {
		securityDao.persist(group);
		return group;
	}
}

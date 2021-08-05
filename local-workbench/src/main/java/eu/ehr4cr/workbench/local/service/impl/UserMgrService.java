package eu.ehr4cr.workbench.local.service.impl;

import com.custodix.insite.local.user.vocabulary.Email;
import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Authority;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.properties.PropertyProvider;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserAcceptMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserAcceptMailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(value = "localTransactionManager",
			   readOnly = true)
class UserMgrService implements IUserMgrService {
	private final SecurityDao securityDao;
	private final MailService mailService;
	private final AccountSecuritySettings accountSecuritySettings;
	private final String urlBase;

	@Autowired
	UserMgrService(SecurityDao securityDao, MailService mailService, AccountSecuritySettings accountSecuritySettings,
			PropertyProvider propertyProvider) {
		this.securityDao = securityDao;
		this.mailService = mailService;
		this.accountSecuritySettings = accountSecuritySettings;
		this.urlBase = propertyProvider.getBaseUrl();
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public User createUser(String email, String username, String password) {
		return securityDao.createUser(email, username, password);
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public Group createGroup(String groupname) {
		Group newGroup = new Group(groupname);
		securityDao.persist(newGroup);
		return newGroup;
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public Authority createAuthority(String innerName) {
		Authority newAuthority = new Authority(innerName);
		securityDao.persist(newAuthority);
		return newAuthority;
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public void deleteUser(Long userId) {
		securityDao.deleteUser(userId);
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public void acceptUser(Long userId, String password) {
		securityDao.acceptUser(userId);
		User user = securityDao.findUserById(userId);
		checkUserGroups(user);
		user.activate(password);
		securityDao.merge(user);
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public void acceptUser(Long userId) {
		securityDao.acceptUser(userId);
		User user = securityDao.findUserById(userId);
		checkUserGroups(user);
		user.invite(accountSecuritySettings.getInviteExpireValue(), accountSecuritySettings.getInviteExpireUnit());
		UserAcceptMailContent mailContent = ImmutableUserAcceptMailContent.builder()
				.acceptExpireValue(accountSecuritySettings.getInviteExpireValue())
				.acceptExpireUnit(accountSecuritySettings.getInviteExpireUnit())
				.acceptAcceptUrl(constructCompleteAccountUrl(user))
				.build();
		securityDao.merge(user);
		mailService.sendMail(mailContent, user.getEmail());
	}

	private void checkUserGroups(User user) {
		if (!user.hasGroups()) {
			addDefaultGroups(user);
		}
	}

	private void addDefaultGroups(User user) {
		assignUserToGroups(user.getId(), Arrays.asList(GroupType.USR));
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   readOnly = false)
	public void assignUserToGroups(Long userId, List<GroupType> groups) {
		User user = securityDao.findUserById(userId);
		user.setGroups(groups, findAllGroups());
		securityDao.merge(user);
	}

	@Override
	public boolean userHasAuthority(Long userId, AuthorityType authorityType) {
		return securityDao.userHasAuthority(userId, authorityType);
	}

	@Override
	public boolean userAlreadyExists(String email, String username) {
		return securityDao.userAlreadyExists(email, username);
	}

	@Override
	public User findUserByUsername(String userName) {
		return securityDao.findUserByUsername(userName);
	}

	@Override
	public User findUserById(Long userId) {
		return securityDao.findUserById(userId);
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		return securityDao.findUserByEmail(Email.of(email));
	}

	@Override
	public Group findGroupByGroupname(String groupname) {
		return securityDao.findGroupByGroupname(groupname);
	}

	@Override
	public Authority findAuthorityByName(String innerName) {
		return securityDao.findAuthorityByName(innerName);
	}

	@Override
	public List<User> findAllUsers() {
		return securityDao.findAllUsers();
	}

	@Override
	public List<Group> findAllGroups() {
		return securityDao.findAllGroups();
	}

	@Override
	public List<User> findUsersByGroupname(String innerName) {
		return securityDao.findUsersByGroupname(innerName);
	}

	@Override
	public List<User> findValidUsers() {
		return securityDao.findAllValidUsers();
	}

	@Override
	public List<User> findInvalidUsers() {
		return securityDao.findAllUsers();
	}

	private String constructCompleteAccountUrl(User user) {
		return constructCompleteUrl(user, WebRoutes.completeInvitation);
	}

	private String constructCompleteUrl(User user, String route) {
		return urlBase + route + "?userId=" + user.getId() + "&password=" + user.getTempPassword();
	}
}

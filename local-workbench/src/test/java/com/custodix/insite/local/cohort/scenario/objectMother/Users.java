package com.custodix.insite.local.cohort.scenario.objectMother;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.custodix.insite.local.cohort.scenario.UserGroup;
import com.custodix.insite.local.user.vocabulary.Password;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.*;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.service.TestTimeService;

public class Users {
	public static final String PASSWORD = "P4ssword_";
	private static final Password PASSWORD_CHANGED = Password.of(PASSWORD + "_changed");
	private static final String SECURITY_QUESTION_ID = "1";
	private static final String SECURITY_QUESTION_ANSWER = "What was your childhood nickname?";

	private final SecurityDao securityDao;
	private final AccountSecuritySettings accountSecuritySettings;
	private final PasswordExpirySettings passwordExpirySettings;
	private final TestTimeService testTimeService;

    public Users(SecurityDao securityDao, AccountSecuritySettings accountSecuritySettings,
			PasswordExpirySettings passwordExpirySettings, TestTimeService testTimeService) {
		this.securityDao = securityDao;
		this.accountSecuritySettings = accountSecuritySettings;
		this.passwordExpirySettings = passwordExpirySettings;
		this.testTimeService = testTimeService;
	}

	public User persist(User user) {
		User persistedUser = securityDao.persist(user);
		securityDao.flush();
		return persistedUser;
	}

	public User anAuthor() {
		return createUserYesterday("Author", "author@custodix.com", PASSWORD, true);
	}

	public User aRegularUser() {
		return createUserYesterday("Regular User", "regularUser@custodix.com", PASSWORD, true);
	}

	public User anActiveUser() {
		return createUserYesterday("Active User", "activeUser@custodix.com", PASSWORD, true);
	}

	public User aPendingUser() {
		return createUserYesterday("Pending User", "pendingUser@custodix.com", "", false);
	}

	public User anInvitedUser() {
		User user = createUserYesterday("Invited User", "invitedUser@custodix.com", "", false);
		inviteUser(user);
		return persist(user);
	}

	public User aDrmUser() {
    	return aUser(UserGroup.DRM);
	}


	public User anInviteExpiredUser() {
		testTimeService.timeTravelToThePast(accountSecuritySettings.getInviteExpireValue() + 1,
				accountSecuritySettings.getInviteExpireUnit());
		User user = createUser("Invite Expired User", "inviteExpiredUser@custodix.com", "", false);
		inviteUser(user);
		testTimeService.reset();
		return persist(user);
	}

	public User aRecoveringUser() {
		User user = createUserYesterday("Recovering User", "recoveringUser@custodix.com", PASSWORD, true);
		recoverUser(user);
		return persist(user);
	}

	public User aRecoveryExpiredUser() {
		testTimeService.timeTravelToThePast(accountSecuritySettings.getInviteExpireValue() + 1,
				accountSecuritySettings.getInviteExpireUnit());
		User user = createUser("Recovery Expired User", "recoveryExpiredUser@custodix.com", PASSWORD, true);
		recoverUser(user);
		testTimeService.reset();
		return persist(user);
	}

	public User aDeletedUser() {
		User user = createUserYesterday("Deleted User", "deletedUser@custodix.com", PASSWORD, true);
		securityDao.deleteUser(user.getId());
		return user;
	}

	public User aUser(UserGroup userGroup) {
		User user = new User("Group User", PASSWORD, "groupUser@custodix.com", true);
		user = persist(user);
		assignGroupToUser(user, userGroup);
		return user;
	}

	public User aUser(UserStatus status) {
		switch (status) {
		case PENDING:
			return aPendingUser();
		case ACTIVE:
			return anActiveUser();
		case INVITED:
			return anInvitedUser();
		case EXPIRED_INVITATION:
			return anInviteExpiredUser();
		case RECOVERING:
			return aRecoveringUser();
		case EXPIRED_RECOVERY:
			return aRecoveryExpiredUser();
		case DELETED:
			return aDeletedUser();
		default:
			throw new IllegalStateException("No factory method available for a user in state " + status.name());
		}
	}

	public User aPowerUser() {
		User user = createUserYesterday("Power User", "powerUser@custodix.com", PASSWORD, true);
		assignToPowerUsers(user);
		return user;
	}

	public User aUserWithAuthority(AuthorityType authorityType) {
		return aUserWithAuthorities(singletonList(authorityType));
	}

	public User aUserWithoutAuthority(AuthorityType authorityType) {
		return aUserWithoutAuthorities(singletonList(authorityType));
	}

	public User aUserWithAllAuthorities() {
		return aUserWithoutAuthorities(new ArrayList<>());
	}

	public User aUserWithoutAuthorities() {
		return aUserWithAuthorities(new ArrayList<>());
	}

	public User aUserWithAuthorities(List<AuthorityType> authorityTypes) {
		User user = createUserYesterday("Regular User", "regularUser@custodix.com", PASSWORD, true);
		authorityTypes.forEach(auth -> addAuthorityToUser(auth, user));
		persist(user);
		return user;
	}

	public User aUserWithoutAuthorities(List<AuthorityType> authorityTypes) {
		User user = new User("Regular User", PASSWORD, "regularUser@custodix.com", true);
		Arrays.stream(AuthorityType.values())
				.filter(aType -> !authorityTypes.contains(aType))
				.forEach(auth -> addAuthorityToUser(auth, user));
		persist(user);
		return user;
	}

	public User aUserWithoutAuthorities(AuthorityType... authorityTypes) {
		return aUserWithoutAuthorities(asList(authorityTypes));
	}

	public User aSystemUser() {
		return new SystemUser();
	}

	public void login(User user) {
		SecurityContextUser securityContextUser = new SecurityContextUser(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(securityContextUser,
				securityContextUser.getPassword(), securityContextUser.getAuthorities());
		SecurityContextHolder.getContext()
				.setAuthentication(authentication);
	}

	public void loginAnonymously() {
		Authentication authentication = new AnonymousAuthenticationToken("key", "anonymousUser",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
		SecurityContextHolder.getContext()
				.setAuthentication(authentication);
	}

	public void logout() {
		SecurityContextHolder.clearContext();
	}

	public Password setImminentlyExpiringPasswordOnNextUpdate(User user) {
		testTimeService.timeTravelToThePast(passwordExpirySettings.getImminentDuration());
		changePassword(user, PASSWORD_CHANGED);
		testTimeService.reset();
		return PASSWORD_CHANGED;
	}

	public Password setImminentlyExpiringPassword(User user) {
		Password password = setImminentlyExpiringPasswordOnNextUpdate(user);
		user.updatePasswordStatus(passwordExpirySettings);
		return password;
	}

	public Password setExpiredPassword(User user) {
		testTimeService.timeTravelToThePast(passwordExpirySettings.getExpiryDuration().plusDays(1L));
		changePassword(user, PASSWORD_CHANGED);
		testTimeService.reset();
		user.updatePasswordStatus(passwordExpirySettings);
		return PASSWORD_CHANGED;
	}

	private void changePassword(User user, Password password) {
		user.changePassword(Password.of(PASSWORD), password);
	}

	private User createUserYesterday(String username, String email, String password, boolean enabled) {
		testTimeService.timeTravelToThePast(Duration.ofDays(1L));
		User user = createUser(username, email, password, enabled);
		testTimeService.reset();
		return user;
	}

	private User createUser(String username, String email, String password, boolean enabled) {
		Group userGroup = securityDao.findGroup(GroupType.USR);
		User user = new User(username, email, new HashSet<>(singletonList(userGroup)));
		if (enabled) {
			user.setEnabled(true);
			user.activate(password);
			user.updateSecurityQuestion(SECURITY_QUESTION_ID, SECURITY_QUESTION_ANSWER);
		}
		return persist(user);
	}

	private void inviteUser(User user) {
		user.invite(accountSecuritySettings.getInviteExpireValue(), accountSecuritySettings.getInviteExpireUnit());
	}

	private void recoverUser(User user) {
		user.performRecovery(accountSecuritySettings.getInviteExpireValue(),
				accountSecuritySettings.getInviteExpireUnit(), () -> {
				});
	}

	private User assignToPowerUsers(User user) {
		return assignGroupToUser(user, UserGroup.POWER_USER);
	}

	private User assignGroupToUser(User user, UserGroup userGroup) {
		List<Group> allGroups = securityDao.findAllGroups();
		List<GroupType> newGroups = asList(userGroup)
				.stream()
				.map(this::mapTogroupType)
				.collect(Collectors.toList());
		user.setGroups(newGroups, allGroups);
		securityDao.flush();
		return securityDao.refresh(user);
	}

	private GroupType mapTogroupType(UserGroup userGroup) {
		switch (userGroup) {
		case POWER_USER:
			return GroupType.PWR;
		case DRM:
			return GroupType.DRM;
		case REGULAR_USER:
			return GroupType.USR;
		case ADMINISTRATOR:
			return GroupType.ADM;
		}
		throw new IllegalArgumentException("Unrecognized group: "+userGroup);
	}

	private void addAuthorityToUser(AuthorityType auth, User user) {
		Authority authority = securityDao.findAuthorityByName(auth.getInnerName());
		user.addAuthority(authority);
	}
}

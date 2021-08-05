package eu.ehr4cr.workbench.local.model.security;

import static eu.ehr4cr.workbench.local.Constants.STRING_SHORT;
import static eu.ehr4cr.workbench.local.eventpublisher.DomainEventPublisher.publishEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.SQLDelete;

import com.custodix.insite.local.shared.exceptions.UserException;
import com.custodix.insite.local.user.domain.events.PasswordRecoveredEvent;
import com.custodix.insite.local.user.vocabulary.AuthenticateResult;
import com.custodix.insite.local.user.vocabulary.Email;
import com.custodix.insite.local.user.vocabulary.Password;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.BaseEntity;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@Entity
@Table(name = "app_user")
@SQLDelete(sql = "UPDATE app_user SET deleted = true WHERE id = ? AND version = ?")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Embedded
	private UserCredential credential = new UserCredential();

	@Size(max = STRING_SHORT)
	@Column(name = "username",
			unique = true,
			nullable = false,
			length = STRING_SHORT)
	private String username;
	@Size(max = STRING_SHORT)
	@Column(name = "first_name",
			length = STRING_SHORT)
	private String firstName;
	@Size(max = STRING_SHORT)
	@Column(name = "last_name",
			length = STRING_SHORT)
	private String lastName;
	@Size(max = STRING_SHORT)
	@Column(name = "email",
			length = STRING_SHORT)
	private String email;

	@Embedded
	private SecurityQuestion securityQuestion;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_authority",
			   joinColumns = { @JoinColumn(name = "user_id") },
			   inverseJoinColumns = { @JoinColumn(name = "authority_id") })
	private Set<Authority> authorities = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usergroup_user",
			   joinColumns = { @JoinColumn(name = "user_id") },
			   inverseJoinColumns = { @JoinColumn(name = "group_id") })
	private Set<Group> groups = new HashSet<>();

	public User() {
	}

	public User(String username, String email, Set<Group> groups) {
		this.username = username;
		this.email = email;
		this.credential = new UserCredential("", false);
		initGroups(groups);
	}

	public User(String username, String plainPassword) {
		this.username = username;
		this.credential = new UserCredential(plainPassword, false);
	}

	public User(String username, String plainPassword, String email) {
		this.username = username;
		this.email = email;
		this.credential = new UserCredential(plainPassword, false);
	}

	public User(String username, String plainPassword, String email, boolean enabled) {
		this.username = username;
		this.email = email;
		this.credential = new UserCredential(plainPassword, enabled);
	}

	public UserIdentifier getIdentifier() {
		return UserIdentifier.of(getId());
	}

	public void invite(Integer inviteExpireValue, TimeUnit inviteExpireUnit) {
		credential.invite(inviteExpireValue, inviteExpireUnit);
	}

	public void reinvite(Integer inviteExpireValue, TimeUnit inviteExpireUnit) {
		if (getStatus().isReinvitable()) {
			credential.invite(inviteExpireValue, inviteExpireUnit);
		} else {
			throw new IllegalStateException("User state is invalid for reinvitation");
		}
	}

	public void activate(String password) {
		credential.activate(password);
	}

	public void activate(Password password, String tempPassword) {
		credential.activate(password.getValue(), tempPassword);
	}

	public void recover(Password password, String tempPassword) {
		credential.recover(password.getValue(), tempPassword);
		publishPasswordRecoveredEvent();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void changePassword(Password currentPassword, Password newPassword) {
		credential.changePassword(currentPassword.getValue(), newPassword.getValue());
	}

	public AuthenticateResult authenticate(Password password) {
		return credential.authenticate(password.getValue());
	}

	public Date getPasswordLastModified() {
		return credential.getPasswordLastModified();
	}

	public void updatePasswordStatus(PasswordExpirySettings passwordExpirySettings) {
		credential.updatePasswordStatus(passwordExpirySettings, email);
	}

	public boolean isPasswordImminentlyExpiring() {
		return credential.isPasswordImminentlyExpiring();
	}

	public Optional<Date> findPasswordExpiryDate(PasswordExpirySettings passwordExpirySettings) {
		return credential.findPasswordExpiryDate(passwordExpirySettings);
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Email getEmailAddress() {
		return Email.of(email);
	}

	/**
	 * @deprecated  Use {@link #getEmailAddress()} instead.
	 * When all usages have been removed, {@link #getEmailAddress()} can be renamed to 'getEmail()'
	 */
	@Deprecated
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserStatus getStatus() {
		return credential.getStatus();
	}

	public boolean addAuthority(Authority authority) {
		return authorities.add(authority);
	}

	public boolean removeAuthority(Authority authority) {
		return authorities.remove(authority);
	}

	public boolean isEnabled() {
		return credential.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		credential.setEnabled(enabled);
	}

	public void delete() {
		credential.delete();
	}

	public boolean isExpired() {
		return credential.isExpired();
	}

	public boolean isRecoverable() {
		return credential.isRecoverable();
	}

	public String getTempPassword() {
		return credential.getTempPassword();
	}

	public Date getTempPasswordExpirationDate() {
		return credential.getTempPasswordExpirationDate();
	}

	public boolean isAdministratorUser() {
		for (Group group : groups) {
			if (group.getName()
					.equals(GroupType.ADM.getInnerName())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAuthority(AuthorityType authorityType) {
		return hasIndividualAuthority(authorityType) || hasGroupAuthority(authorityType);
	}

	public void setGroups(List<GroupType> newGroups, List<Group> allGroups) {
		allGroups.forEach(g -> {
			if (newGroups.contains(g.getType())) {
				addGroup(g);
			} else if (GroupType.USR != g.getType()) {
				removeGroup(g);
			}
		});
	}

	private void initGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void removeGroup(Group group) {
		groups.remove(group);
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	public boolean isPartOf(Group group) {
		return groups.contains(group);
	}

	public boolean hasGroups() {
		return !groups.isEmpty();
	}

	public boolean hasSecurityQuestion() {
		return securityQuestion != null;
	}

	public String getSecurityQuestionId() {
		if (hasSecurityQuestion()) {
			return securityQuestion.getSecurityQuestionId();
		} else {
			return "";
		}
	}

	public String getSecurityAnswer() {
		if (hasSecurityQuestion()) {
			return securityQuestion.getSecurityAnswer();
		} else {
			return "";
		}
	}

	public void updateSecurityQuestion(String securityQuestionId, String securityAnswer) {
		this.securityQuestion = new SecurityQuestion(securityQuestionId, securityAnswer);
	}

	public void performSecurityQuestionRecovery(Integer expireValue, TimeUnit expireUnit, String answer,
			Runnable recoveryCallback) {
		if (isRecoverable() && hasSecurityQuestion()) {
			verifySecurityQuestion(answer, () -> doRecovery(expireValue, expireUnit, recoveryCallback));
		} else {
			throwRecoveryUnavailable();
		}
	}

	private void verifySecurityQuestion(String answer, Runnable recoveryCallback) {
		if (securityQuestion.matches(answer)) {
			recoveryCallback.run();
		} else {
			throw new UserException("Incorrect security question answer");
		}
	}

	public void performRecovery(Integer expireValue, TimeUnit expireUnit, Runnable recoveryCallback) {
		if (isRecoverable()) {
			doRecovery(expireValue, expireUnit, recoveryCallback);
		} else {
			throwRecoveryUnavailable();
		}
	}

	private void doRecovery(Integer expireValue, TimeUnit expireUnit, Runnable recoveryCallback) {
		credential.enableRecovery(expireValue, expireUnit);
		recoveryCallback.run();
	}

	private boolean hasIndividualAuthority(AuthorityType authorityType) {
		return authorities.stream()
				.anyMatch(a -> a.isType(authorityType));
	}

	private boolean hasGroupAuthority(AuthorityType authorityType) {
		return groups.stream()
				.anyMatch(g -> g.hasAuthority(authorityType));
	}

	private void throwRecoveryUnavailable() {
		throw new UserException("Recovery unavailable for this account");
	}

	private void publishPasswordRecoveredEvent() {
		PasswordRecoveredEvent event = PasswordRecoveredEvent.newBuilder()
				.withEmail(getEmailAddress())
				.build();
		publishEvent(event);
	}
}
package eu.ehr4cr.workbench.local.model.security;

import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.BAD_CREDENTIALS;
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.PASSWORD_EXPIRED;
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.SUCCESS;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.custodix.insite.local.user.vocabulary.AuthenticateResult;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.exception.feasibility.DomainException;
import eu.ehr4cr.workbench.local.exception.security.TempPasswordMismatchException;

@Embeddable
public class UserCredential implements Serializable {
	@Embedded
	private Password password;
	@Embedded
	private TempPassword tempPassword;
	@Column(name = "enabled", nullable = false)
	private boolean enabled = false;
	@Column(name = "deleted", nullable = true)
	private Boolean deleted = false;

	UserCredential() {
		// jpa
	}

	UserCredential(String plainPassword, boolean enabled) {
		this.password = new Password(plainPassword);
		this.enabled = enabled;
	}

	void invite(Integer expireValue, TimeUnit expireUnit) {
		enabled = true;
		tempPassword = new TempPassword(expireValue, expireUnit);
	}

	void activate(String plainPassword) {
		resetPassword(plainPassword);
	}

	void activate(String plainPassword, String tempPassword) {
		if (isTempPasswordValid(tempPassword)) {
			resetPassword(plainPassword);
		} else {
			throw new TempPasswordMismatchException();
		}
	}

	void recover(String plainPassword, String tempPassword) {
		if (isTempPasswordValid(tempPassword)) {
			resetPassword(plainPassword);
		} else {
			throw new TempPasswordMismatchException();
		}
	}

	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	boolean isExpired() {
		return tempPassword != null && tempPassword.isExpired();
	}

	void delete() {
		this.deleted = true;
	}

	Boolean isDeleted() {
		return deleted;
	}

	boolean isPending() {
		return !isDeleted() && !isEnabled() && !hasTempPassword();
	}

	boolean isActive() {
		return !isDeleted() && isEnabled() && !hasTempPassword();
	}

	boolean isInvited() {
		return isInvitedOrRecovering() && !hasPassword() && !isExpired();
	}

	boolean isRecovering() {
		return isInvitedOrRecovering() && hasPassword() && !isExpired();
	}

	boolean isInviteExpired() {
		return isInvitedOrRecovering() && !hasPassword() && isExpired();
	}

	boolean isRecoveryExpired() {
		return isInvitedOrRecovering() && hasPassword() && isExpired();
	}

	UserStatus getStatus() {
		return UserStatus.of(this);
	}

	boolean isRecoverable() {
		return getStatus().isRecoverable();
	}

	void enableRecovery(Integer expireValue, TimeUnit expireUnit) {
		tempPassword = new TempPassword(expireValue, expireUnit);
	}

	void changePassword(String currentPassword, String newPassword) {
		if (!password.isMatchingPassword(currentPassword)) {
			throw new DomainException("Your current password is incorrect");
		}
		if (password.isMatchingPassword(newPassword)) {
			throw new DomainException("Your current and new password must be different");
		}
		password = new Password(newPassword);
	}

	Date getPasswordLastModified() {
		return password.getLastModified();
	}

	void updatePasswordStatus(PasswordExpirySettings passwordExpirySettings, String email) {
		password.updateStatus(passwordExpirySettings, email);
	}

	boolean isPasswordImminentlyExpiring() {
		return password.isImminentlyExpiring();
	}

	Optional<Date> findPasswordExpiryDate(PasswordExpirySettings passwordExpirySettings) {
		return password.findExpiryDate(passwordExpirySettings);
	}

	String getTempPassword() {
		if (hasTempPassword()) {
			return tempPassword.getTempPassword();
		} else {
			return null;
		}
	}

	Date getTempPasswordExpirationDate() {
		if (hasTempPassword()) {
			return tempPassword.getExpirationDate();
		} else {
			return null;
		}
	}

	AuthenticateResult authenticate(String plainPassword) {
		if (!password.isMatchingPassword(plainPassword)) {
			return BAD_CREDENTIALS;
		}
		if (password.isExpired()) {
			return PASSWORD_EXPIRED;
		}
		return SUCCESS;
	}

	private boolean isInvitedOrRecovering() {
		return !isDeleted() && isEnabled() && hasTempPassword();
	}

	private boolean hasPassword() {
		return password.hasPassword();
	}

	private boolean hasTempPassword() {
		return tempPassword != null;
	}

	private void resetPassword(String plainPassword) {
		this.tempPassword = null;
		this.password = new Password(plainPassword);
	}

	private boolean isTempPasswordValid(String tempPassword) {
		return hasTempPassword() && this.tempPassword.matches(tempPassword) && !this.tempPassword.isExpired();
	}
}

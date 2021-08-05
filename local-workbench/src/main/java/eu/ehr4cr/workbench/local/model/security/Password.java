package eu.ehr4cr.workbench.local.model.security;

import static eu.ehr4cr.workbench.local.Constants.STRING_SHORT;
import static eu.ehr4cr.workbench.local.eventpublisher.DomainEventPublisher.publishEvent;
import static eu.ehr4cr.workbench.local.model.security.Password.Status.EXPIRED;
import static eu.ehr4cr.workbench.local.model.security.Password.Status.IMMINENTLY_EXPIRING;
import static eu.ehr4cr.workbench.local.service.DomainTime.now;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.lang3.time.DateUtils.addDays;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.user.domain.events.ImminentlyExpiringPasswordEvent;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.exception.feasibility.DomainException;

@Embeddable
public class Password implements Serializable {
	private static final int ITERATION_NUMBER = 1000;
	private static final String MESSAGE_TAMPERED_PASS_IN_DB = "Database inconsistent salt or digested password altered.";
	private static final String SALT_HASH_SEPARATOR = ":";
	private static final int SALT_HASH_PARTS = 2;
	private static final int SALT_BYTES = 8;

	@Size(max = STRING_SHORT)
	@Column(name = "password",
			length = STRING_SHORT)
	private String password;
	@Column(name = "passwordLastModified")
	private Date lastModified;
	@Enumerated(EnumType.STRING)
	@Column(name = "passwordStatus")
	private Status status;

	Password() {
		// JPA
	}

	Password(String plainPassword) {
		if (!plainPassword.isEmpty()) {
			this.password = calculatePasswordDigest(plainPassword);
			this.lastModified = now();
		} else {
			this.password = plainPassword;
		}
		this.status = Status.ACTIVE;
	}

	boolean hasPassword() {
		return StringUtils.isNotBlank(password);
	}

	Date getLastModified() {
		return lastModified;
	}

	void updateStatus(PasswordExpirySettings passwordExpirySettings, String email) {
		setLastModifiedForLegacyUser();
		Status newStatus = Status.of(this, passwordExpirySettings);
		if (newStatus == IMMINENTLY_EXPIRING && status != IMMINENTLY_EXPIRING) {
			publishImminentlyExpiringPasswordEvent(passwordExpirySettings, email);
		}
		status = newStatus;
	}

	boolean isImminentlyExpiring() {
		return status == IMMINENTLY_EXPIRING;
	}

	boolean isExpired() {
		return status == EXPIRED;
	}

	Optional<Date> findExpiryDate(PasswordExpirySettings passwordExpirySettings) {
		if (canExpire(passwordExpirySettings)) {
			return Optional.of(getExpiryDate(passwordExpirySettings));
		}
		return Optional.empty();
	}

	private boolean canExpire(PasswordExpirySettings passwordExpirySettings) {
		return passwordExpirySettings.isEnabled() && hasPassword();
	}

	private boolean hasLastModified() {
		return lastModified != null;
	}

	private Date getImminentlyExpiringDate(PasswordExpirySettings passwordExpirySettings) {
		return addDays(lastModified, (int) passwordExpirySettings.getImminentDuration()
				.toDays());
	}

	private Date getExpiryDate(PasswordExpirySettings passwordExpirySettings) {
		return addDays(lastModified, (int) passwordExpirySettings.getExpiryDuration()
				.toDays());
	}

	private void publishImminentlyExpiringPasswordEvent(PasswordExpirySettings passwordExpirySettings, String email) {
		ImminentlyExpiringPasswordEvent event = ImminentlyExpiringPasswordEvent.newBuilder()
				.withUserMailAddress(email)
				.withExpirationDate(getExpiryDate(passwordExpirySettings))
				.build();
		publishEvent(event);
	}

	private void setLastModifiedForLegacyUser() {
		if (!hasLastModified() && hasPassword()) {
			lastModified = now();
		}
	}

	private String calculatePasswordDigest(final String plainPassword) {
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// Salt generation 64 bits long
			byte[] bSalt = new byte[SALT_BYTES];
			random.nextBytes(bSalt);
			// Digest computation
			byte[] bDigest = getHash(ITERATION_NUMBER, plainPassword, bSalt);
			String sDigest = encodeBase64String(bDigest);
			String sSalt = encodeBase64String(bSalt);
			return sSalt.concat(SALT_HASH_SEPARATOR)
					.concat(sDigest);
		} catch (NoSuchAlgorithmException e) {
			// SHA1PRNG not available - shouldn't happen
			throw new AssertionError("Could not store hashed password", e);
		}
	}

	boolean isMatchingPassword(String plainPassword) {
		String digest;
		String[] saltNPepa = getHashedPassword().split(SALT_HASH_SEPARATOR, SALT_HASH_PARTS);
		digest = saltNPepa.length > 1 ? saltNPepa[1] : null;
		String salt = saltNPepa[0];
		// DATABASE VALIDATION
		if (digest == null || salt == null) {
			throw new AssertionError(MESSAGE_TAMPERED_PASS_IN_DB);
		}

		byte[] bDigest = decodeBase64(digest);
		byte[] bSalt = decodeBase64(salt);

		// Compute the new DIGEST
		byte[] proposedDigest = getHash(ITERATION_NUMBER, plainPassword, bSalt);

		return Arrays.equals(proposedDigest, bDigest);
	}

	private String getHashedPassword() {
		if (hasPassword()) {
			return password;
		} else {
			return "00000000000=:000000000000000000000000000=";
		}
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 *
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 */
	private byte[] getHash(int iterationNb, String password, byte[] salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(salt);
			byte[] input = digest.digest(password.getBytes("UTF-8"));
			for (int i = 0; i < iterationNb; i++) {
				digest.reset();
				input = digest.digest(input);
			}
			return input;
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("SHA-1 should be supported by default.", e);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 should be supported by default.", e);
		}
	}

	enum Status {
		ACTIVE() {
			@Override
			boolean hasStatus(Password password, PasswordExpirySettings passwordExpirySettings) {
				return !password.canExpire(passwordExpirySettings) || isImminentlyExpiringDateAfterNow(password,
						passwordExpirySettings);
			}
		},
		IMMINENTLY_EXPIRING() {
			@Override
			boolean hasStatus(Password password, PasswordExpirySettings passwordExpirySettings) {
				return password.canExpire(passwordExpirySettings) && !isImminentlyExpiringDateAfterNow(password,
						passwordExpirySettings) && isExpiryDateAfterNow(password, passwordExpirySettings);
			}
		},
		EXPIRED() {
			@Override
			boolean hasStatus(Password password, PasswordExpirySettings passwordExpirySettings) {
				return password.canExpire(passwordExpirySettings) && !isExpiryDateAfterNow(password,
						passwordExpirySettings);
			}
		};

		public static Status of(Password password, PasswordExpirySettings passwordExpirySettings) {
			return Arrays.stream(values())
					.filter(s -> s.hasStatus(password, passwordExpirySettings))
					.findFirst()
					.orElseThrow(() -> new DomainException("No valid password status found"));
		}

		abstract boolean hasStatus(Password password, PasswordExpirySettings passwordExpirySettings);

		private static boolean isImminentlyExpiringDateAfterNow(Password password,
				PasswordExpirySettings passwordExpirySettings) {
			return password.getImminentlyExpiringDate(passwordExpirySettings)
					.after(now());
		}

		private static boolean isExpiryDateAfterNow(Password password, PasswordExpirySettings passwordExpirySettings) {
			return password.getExpiryDate(passwordExpirySettings)
					.after(now());
		}
	}
}

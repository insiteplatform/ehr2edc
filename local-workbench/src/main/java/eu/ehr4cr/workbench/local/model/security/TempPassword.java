package eu.ehr4cr.workbench.local.model.security;

import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TempPassword implements Serializable {
	@Column(name = "tempPassword")
	private String tempPassword;
	@Column(name = "expirationDate")
	private Date expirationDate;

	TempPassword() {
		// JPA
	}

	TempPassword(Integer expireValue, TimeUnit expireUnit) {
		this.tempPassword = generate();
		this.expirationDate = calculateExpirationDate(expireValue, expireUnit);
	}

	boolean matches(String tempPassword) {
		return this.tempPassword.equals(tempPassword);
	}

	String getTempPassword() {
		return tempPassword;
	}

	boolean isExpired() {
		return expirationDate.before(now());
	}

	Date getExpirationDate() {
		return expirationDate;
	}

	private String generate() {
		return UUID.randomUUID()
				.toString();
	}

	private Date calculateExpirationDate(Integer expireValue, TimeUnit expireUnit) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now().getTime() + expireUnit.toMillis(expireValue));
		return cal.getTime();
	}
}

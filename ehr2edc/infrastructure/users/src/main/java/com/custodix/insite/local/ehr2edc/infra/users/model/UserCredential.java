package com.custodix.insite.local.ehr2edc.infra.users.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserCredential implements Serializable {
	@Column(name = "enabled",
			nullable = false)
	private boolean enabled = false;
	@Column(name = "deleted",
			nullable = true)
	private Boolean deleted = false;

	UserCredential() {
		// jpa
	}

	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	void delete() {
		this.deleted = true;
	}

	Boolean isDeleted() {
		return deleted;
	}

}

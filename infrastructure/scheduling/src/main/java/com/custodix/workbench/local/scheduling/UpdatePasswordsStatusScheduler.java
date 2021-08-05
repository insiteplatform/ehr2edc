package com.custodix.workbench.local.scheduling;

import org.springframework.scheduling.annotation.Scheduled;

import com.custodix.insite.local.user.application.api.UpdatePasswordsStatus;

class UpdatePasswordsStatusScheduler {
	private static final int TEN_MINUTES = 600_000;
	private final UpdatePasswordsStatus updatePasswordsStatus;

	UpdatePasswordsStatusScheduler(UpdatePasswordsStatus updatePasswordsStatus) {
		this.updatePasswordsStatus = updatePasswordsStatus;
	}

	@Scheduled(fixedDelay = TEN_MINUTES)
	public void run() {
		updatePasswordsStatus.updatePasswordsStatus();
	}
}

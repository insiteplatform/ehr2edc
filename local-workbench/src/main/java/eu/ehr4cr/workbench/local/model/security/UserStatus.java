package eu.ehr4cr.workbench.local.model.security;

import java.util.Arrays;

public enum UserStatus {
	ACTIVE(true, false) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isActive();
		}
	}, PENDING(false, false) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isPending();
		}
	}, INVITED(false, true) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isInvited();
		}
	}, EXPIRED_INVITATION(false, true) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isInviteExpired();
		}
	}, RECOVERING(true, false) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isRecovering();
		}
	}, EXPIRED_RECOVERY(true, false) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return userCredential.isRecoveryExpired();
		}
	}, DELETED(false, false) {
		@Override
		boolean hasStatus(UserCredential userCredential) {
			return userCredential.isDeleted();
		}
	}
	, UNKNOWN(false, false) {
		@Override
		public boolean hasStatus(UserCredential userCredential) {
			return false;
		}
	};

	private final boolean recoverable;
	private final boolean reinvitable;

	UserStatus(boolean recoverable, boolean reinvitable) {
		this.recoverable = recoverable;
		this.reinvitable = reinvitable;
	}

	abstract boolean hasStatus(UserCredential userCredential);

	public boolean isRecoverable() {
		return recoverable;
	}

	public boolean isReinvitable() {
		return reinvitable;
	}

	static UserStatus of(UserCredential userCredential) {
		return Arrays.stream(values())
				.filter(s -> s.hasStatus(userCredential))
				.findFirst()
				.orElse(UNKNOWN);
	}
}

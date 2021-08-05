package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus;

import java.util.Optional;

public enum VitalStatus {
	ALIVE{
		@Override
		public Optional<Boolean> isDeceased() {
			return Optional.of(false);
		}
	},
	UNKNOWN {
		@Override
		public Optional<Boolean> isDeceased() {
			return Optional.empty();
		}
	},
	DECEASED {
		@Override
		public Optional<Boolean> isDeceased() {
			return Optional.of(true);
		}
	};

	public abstract Optional<Boolean> isDeceased();
}

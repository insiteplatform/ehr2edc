package com.custodix.insite.local.ehr2edc.vocabulary;

import static com.custodix.insite.local.ehr2edc.vocabulary.PopulationNotReadyReason.SUBJECT_MIGRATION_STARTED;

public final class EventPopulationReadiness {

	private final boolean ready;
	private final PopulationNotReadyReason notReadyReason;
	private final boolean subjectMigrationInProgress;

	private EventPopulationReadiness(Builder builder) {
		ready = builder.ready;
		notReadyReason = builder.notReadyReason;
		subjectMigrationInProgress = builder.subjectMigrationInProgress;
	}

	public static EventPopulationReadiness createForEHRNotRegistered() {
		return EventPopulationReadiness.newBuilder()
				.withReady(false)
				.withNotReadyReason(SUBJECT_MIGRATION_STARTED)
				.withSubjectMigrationInProgress(true)
				.build();
	}

	public static EventPopulationReadiness createForEHRRegistered() {
		return EventPopulationReadiness.newBuilder()
				.withReady(true)
				.withSubjectMigrationInProgress(false)
				.build();
	}

	public boolean isReady() {
		return ready;
	}

	public boolean isSubjectMigrationInProgress() {
		return subjectMigrationInProgress;
	}

	public PopulationNotReadyReason getNotReadyReason() {
		return notReadyReason;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public boolean isNotReady() {
		return !isReady();
	}

	public static final class Builder {
		private boolean ready;
		private PopulationNotReadyReason notReadyReason;
		private boolean subjectMigrationInProgress;

		private Builder() {
		}

		public Builder withReady(final boolean val) {
			ready = val;
			return this;
		}

		public Builder withNotReadyReason(final PopulationNotReadyReason val) {
			notReadyReason = val;
			return this;
		}

		public Builder withSubjectMigrationInProgress(boolean val) {
			subjectMigrationInProgress = val;
			return this;
		}

		public EventPopulationReadiness build() {
			return new EventPopulationReadiness(this);
		}
	}
}

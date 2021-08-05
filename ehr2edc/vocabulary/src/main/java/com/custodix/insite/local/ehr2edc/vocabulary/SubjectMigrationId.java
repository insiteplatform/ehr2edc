package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

public final class SubjectMigrationId {
	private final String id;

	private SubjectMigrationId(String id) {
		this.id = id;
	}

	public static SubjectMigrationId of(final String id) {
		return new SubjectMigrationId(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SubjectMigrationId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubjectMigrationId subjectId = (SubjectMigrationId) o;
		return Objects.equals(id, subjectId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

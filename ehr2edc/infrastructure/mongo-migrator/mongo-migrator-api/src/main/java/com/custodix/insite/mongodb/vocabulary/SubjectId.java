package com.custodix.insite.mongodb.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class SubjectId {

	@NotBlank
	private final String id;

	private SubjectId(String id) {
		this.id = id;
	}


	public static SubjectId of(String id) {
		return new SubjectId(id);
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubjectId subjectId = (SubjectId) o;
		return Objects.equals(id, subjectId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}

package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public interface GetStudy {

	@Allow(AUTHENTICATED)
	Response getStudy(@Valid @NotNull Request request);

	final class Request {

		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;

		private Request(Builder builder) {
			studyId = builder.studyId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public static final class Builder {
			private StudyId studyId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {

		private final Study study;

		private Response(Builder builder) {
			study = builder.study;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Study getStudy() {
			return study;
		}

		public static final class Builder {
			private Study study;

			private Builder() {
			}

			public Builder withStudy(Study study) {
				this.study = study;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Study {
		private final StudyId id;
		private final String name;
		private final String description;
		private final Collection<Investigator> investigators;
		private final Collection<Subject> subjects;
		private final Permissions permissions;

		private Study(Builder builder) {
			id = builder.id;
			name = builder.name;
			description = builder.description;
			investigators = builder.investigators == null ? Collections.emptyList() : builder.investigators;
			subjects = builder.subjects == null ? Collections.emptyList() : builder.subjects;
			permissions = builder.permissions;
		}

		public Collection<Subject> getSubjects() {
			return subjects;
		}

		public Collection<Investigator> getInvestigators() {
			return investigators;
		}

		public StudyId getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Permissions getPermissions() {
			return permissions;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId id;
			private String name;
			private String description;
			private Collection<Investigator> investigators;
			private Collection<Subject> subjects;
			private Permissions permissions;

			private Builder() {
			}

			public Builder withId(StudyId id) {
				this.id = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withDescription(String description) {
				this.description = description;
				return this;
			}

			public Builder withInvestigators(Collection<Investigator> investigators) {
				this.investigators = investigators;
				return this;
			}

			public Builder withSubjects(Collection<Subject> subjects) {
				this.subjects = subjects;
				return this;
			}

			public Builder withPermissions(Permissions permissions) {
				this.permissions = permissions;
				return this;
			}

			public Study build() {
				return new Study(this);
			}
		}
	}

	final class Investigator {
		private final UserIdentifier id;
		private final String name;

		private final boolean removable;

		private Investigator(Builder builder) {
			id = builder.id;
			name = builder.name;
			removable = builder.removable;
		}

		public UserIdentifier getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public boolean isRemovable() {
			return removable;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private UserIdentifier id;
			private String name;
			private boolean removable;

			private Builder() {
			}

			public Builder withId(UserIdentifier id) {
				this.id = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withRemovable(boolean removable) {
				this.removable = removable;
				return this;
			}

			public Investigator build() {
				return new Investigator(this);
			}
		}
	}

	final class Subject {
		private final SubjectId subjectId;
		private final EDCSubjectReference edcSubjectReference;
		private final PatientCDWReference patientId;
		private final LocalDate consentDateTime;

		private Subject(Builder builder) {
			subjectId = builder.subjectId;
			edcSubjectReference = builder.edcSubjectReference;
			patientId = builder.patientId;
			consentDateTime = builder.consentDateTime;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EDCSubjectReference getEdcSubjectReference() {
			return edcSubjectReference;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public LocalDate getConsentDateTime() {
			return consentDateTime;
		}

		public static final class Builder {
			private SubjectId subjectId;
			private EDCSubjectReference edcSubjectReference;
			private PatientCDWReference patientId;
			private LocalDate consentDateTime;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withEdcSubjectReference(final EDCSubjectReference val) {
				edcSubjectReference = val;
				return this;
			}

			public Builder withPatientId(final PatientCDWReference val) {
				patientId = val;
				return this;
			}

			public Builder withConsentDateTime(final LocalDate val) {
				consentDateTime = val;
				return this;
			}

			public Subject build() {
				return new Subject(this);
			}
		}
	}

	final class Permissions {
		private final boolean canSubjectsBeAdded;
		private final boolean canSubjectsBeViewed;
		private final boolean canSendToEDC;

		private Permissions(Builder builder) {
			canSubjectsBeAdded = builder.canSubjectsBeAdded;
			canSubjectsBeViewed = builder.canSubjectsBeViewed;
			canSendToEDC = builder.canSendToEDC;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public boolean isCanSubjectsBeAdded() {
			return canSubjectsBeAdded;
		}

		public boolean isCanSubjectsBeViewed() {
			return canSubjectsBeViewed;
		}

		public boolean isCanSendToEDC() {
			return canSendToEDC;
		}

		public static final class Builder {
			private boolean canSubjectsBeAdded;
			private boolean canSubjectsBeViewed;
			private boolean canSendToEDC;

			private Builder() {
			}

			public Builder withCanSubjectsBeAdded(boolean canSubjectsBeAdded) {
				this.canSubjectsBeAdded = canSubjectsBeAdded;
				return this;
			}

			public Builder withCanSubjectsBeViewed(boolean canSubjectsBeViewed) {
				this.canSubjectsBeViewed = canSubjectsBeViewed;
				return this;
			}

			public Builder withCanSendToEDC(boolean canSendToEDC) {
				this.canSendToEDC = canSendToEDC;
				return this;
			}

			public Permissions build() {
				return new Permissions(this);
			}
		}
	}
}
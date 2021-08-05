package com.custodix.insite.local.ehr2edc;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.custodix.insite.local.ehr2edc.Study.DeregisterSubjectData;
import com.custodix.insite.local.ehr2edc.Study.RegisterServices;
import com.custodix.insite.local.ehr2edc.Study.SubjectRegistrationData;
import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class Subjects {
	private final Collection<Subject> items;

	private Subjects(Collection<Subject> items) {
		this.items = items;
	}

	private Subjects(Builder builder) {
		items = builder.items;
	}

	public Collection<SubjectSnapshot> toSnapshot() {
		return items.stream()
				.map(Subject::toSnapshot)
				.collect(toList());
	}

	void updateEHRSubjectRegistrationStatus(SubjectId subjectId, EHRRegistrationStatus status) {
		getSubject(subjectId).updateEHRRegistrationStatus(status);
	}

	PopulatedEvent populate(SubjectId subjectId, PopulationSpecification.Builder populationSpecificationBuilder,
			Function<PopulationSpecification, PopulatedEvent> population) {
		return getSubject(subjectId).populate(populationSpecificationBuilder, population);
	}

	SubjectProjection getSubject(PatientCDWReference patientCDWReference) {
		return items.stream()
				.filter(Subject::isRegistered)
				.filter(item -> item.getPatientCDWReference()
						.equals(patientCDWReference))
				.findFirst()
				.orElseThrow(() -> DomainException.of("study.subject.unknown.cdw", patientCDWReference.getId()));
	}

	List<SubjectProjection> getActive() {
		return items.stream()
				.filter(Subject::isActive)
				.collect(toList());
	}

	List<EDCSubjectReference> getEdcSubjectReferences() {
		return items.stream()
				.filter(Subject::isRegistered)
				.map(Subject::getEdcSubjectReference)
				.collect(toList());
	}

	Optional<EDCSubjectReference> findEDCSubjectReference(final SubjectId subjectId) {
		return items.stream()
				.filter(item -> item.getSubjectId()
						.equals(subjectId))
				.map(Subject::getEdcSubjectReference)
				.findFirst();
	}

	Subject getSubject(final SubjectId subjectId) {
		return findSubject(subjectId).orElseThrow(() -> DomainException.of("study.subject.unknown", subjectId.getId()));
	}

	private Optional<Subject> findSubject(final SubjectId subjectId) {
		return items.stream()
				.filter(item -> item.getSubjectId()
						.equals(subjectId))
				.findFirst();
	}

	static Subjects restoreFrom(Collection<SubjectSnapshot> subjectSnapshots) {
		return subjectSnapshots.stream()
				.map(Subject::restoreFromSnapshot)
				.collect(collectingAndThen(toList(), Subjects::new));
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private Optional<Subject> find(EDCSubjectReference edcSubjectReference) {
		return items.stream()
				.filter(s -> edcSubjectReference.equals(s.getEdcSubjectReference()))
				.findFirst();
	}

	SubjectId register(SubjectRegistrationData subjectRegistrationData, RegisterServices registerServices) {
		Optional<Subject> existingSubject = find(subjectRegistrationData.edcSubjectReference);

		if (existingSubject.isPresent()) {
			handleExistingSubjectRegistration(subjectRegistrationData, existingSubject.get());
			return existingSubject.get().getSubjectId();
		}

		return createNewSubject(subjectRegistrationData, registerServices);
	}

	private void handleExistingSubjectRegistration(SubjectRegistrationData subjectRegistrationData,
			Subject existingSubject) {
		assertPatientReferenceNotUsedByOtherSubject(subjectRegistrationData);

		if (isSamePatient(subjectRegistrationData, existingSubject)) {
			existingSubject.reregister(subjectRegistrationData);
		} else {
			throw DomainException.of("study.subjects.reregister.notdifferentcdw",
					subjectRegistrationData.edcSubjectReference.getId());
		}
	}

	private boolean isSamePatient(SubjectRegistrationData registerData, Subject existing) {
		return existing.getPatientCDWReference()
				.equals(registerData.patientCDWReference);
	}

	void deregister(DeregisterSubjectData deregisterSubjectData) {
		Subject existing = getSubject(deregisterSubjectData.subjectId);
		existing.deregister(deregisterSubjectData);
	}

	boolean isRegistered(PatientCDWReference patientId) {
		return items.stream()
				.filter(s -> patientId.equals(s.getPatientCDWReference()))
				.filter(s -> nonNull(s.getDateOfConsent()))
				.filter(s -> isNull(s.getDateOfConsentWithdrawn()))
				.anyMatch(s -> isNull(s.getDeregisterReason()));
	}

	private SubjectId createNewSubject(SubjectRegistrationData subjectRegistrationData, RegisterServices registerServices) {
		assertPatientReferenceNotUsedByOtherSubject(subjectRegistrationData);
		assertEdcSubjectReferenceNotAlreadyRegistered(subjectRegistrationData.edcSubjectReference);
		assertPatientExists(subjectRegistrationData, registerServices.patientEHRGateway);

		final Subject subject = Subject.initialRegistration(subjectRegistrationData);
		items.add(subject);
		return subject.getSubjectId();
	}

	private void assertPatientReferenceNotUsedByOtherSubject(SubjectRegistrationData subjectRegistrationData) {
		Predicate<Subject> hasSamePatientRef = s -> s.getPatientCDWReference()
				.equals(subjectRegistrationData.patientCDWReference);
		Predicate<Subject> hasSameSubjectRef = s -> s.sameEdcReference(subjectRegistrationData.edcSubjectReference);
		if (items.stream()
				.filter(Subject::isRegistered)
				.filter(hasSameSubjectRef.negate())
				.anyMatch(hasSamePatientRef)) {
			throw DomainException.of("study.subject.duplicate.cdw",
					subjectRegistrationData.patientCDWReference.getId());
		}
	}

	private void assertEdcSubjectReferenceNotAlreadyRegistered(EDCSubjectReference edcSubjectReference) {
		if (items.stream()
				.anyMatch(s -> s.sameEdcReference(edcSubjectReference))) {
			throw DomainException.of("study.subject.unknown.edc", edcSubjectReference.getId());
		}
	}

	private void assertPatientExists(SubjectRegistrationData subjectRegistrationData, PatientEHRGateway patientEHRGateway) {
		if (!patientEHRGateway.exists(subjectRegistrationData.studyId,
				getPatientSearchCriteria(subjectRegistrationData))) {
			throw DomainException.of("study.subject.unknown.cdw",
					subjectRegistrationData.patientCDWReference.getSource(),
					subjectRegistrationData.patientCDWReference.getId(),
					subjectRegistrationData.lastName,
					subjectRegistrationData.firstName,
					subjectRegistrationData.birthDate != null ? subjectRegistrationData.birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE): ""
			);
		}
	}

	private PatientSearchCriteria getPatientSearchCriteria(SubjectRegistrationData subjectRegistrationData) {
		return PatientSearchCriteria.newBuilder()
				.withPatientCDWReference(subjectRegistrationData.patientCDWReference)
				.withBirthDate(subjectRegistrationData.birthDate)
				.withFirstName(subjectRegistrationData.firstName)
				.withLastName(subjectRegistrationData.lastName)
				.build();
	}

	public static final class Builder {
		private Collection<Subject> items = new ArrayList<>();

		private Builder() {
		}

		public Builder withSubjects(Collection<Subject> subjects) {
			this.items = subjects;
			return this;
		}

		public Subjects build() {
			return new Subjects(this);
		}
	}
}

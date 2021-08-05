package com.custodix.insite.local.ehr2edc;

import static com.custodix.insite.local.ehr2edc.DomainEventPublisher.publishEvent;
import static com.custodix.insite.local.ehr2edc.DomainTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class Subject implements SubjectProjection {
	private final PatientCDWReference patientCDWReference;
	private final SubjectId subjectId;
	private final EDCSubjectReference edcSubjectReference;

	private EHRRegistrationStatus ehrRegistrationStatus;
	private LocalDate dateOfConsent;
	private LocalDate dateOfConsentWithdrawn;
	private DeregisterReason deregisterReason;

	public static Subject initialRegistration(Study.SubjectRegistrationData subjectRegistrationData) {
		return new Subject(subjectRegistrationData);
	}

	private Subject(Study.SubjectRegistrationData subjectRegistrationData) {
		subjectId = SubjectId.generate();
		patientCDWReference = subjectRegistrationData.patientCDWReference;
		edcSubjectReference = subjectRegistrationData.edcSubjectReference;
		ehrRegistrationStatus = EHRRegistrationStatus.NOT_REGISTERED;
		register(subjectRegistrationData);
	}

	void updateEHRRegistrationStatus(EHRRegistrationStatus status) {
		this.ehrRegistrationStatus = status;
	}

	private void assertReadyForPopulation() {
		if(isNotEHRRegistered()) {
			throw DomainException.of("study.subject.not.ready.for.population.migration.in.progress",
					subjectId.getId());
		}
	}

	EventPopulationReadiness getEventPopulationReadiness() {
		return isEHRRegistered() ? EventPopulationReadiness.createForEHRRegistered() : EventPopulationReadiness.createForEHRNotRegistered();
	}

	private boolean isNotEHRRegistered() {
		return !isEHRRegistered();
	}

	private boolean isEHRRegistered() {
		return this.ehrRegistrationStatus == EHRRegistrationStatus.REGISTERED;
	}

	static Subject restoreFromSnapshot(SubjectSnapshot snapshot) {
		return new Subject(snapshot);
	}

	private Subject(SubjectSnapshot snapshot) {
		this.patientCDWReference = snapshot.getPatientCDWReference();
		this.subjectId = snapshot.getSubjectId();
		this.edcSubjectReference = snapshot.getEdcSubjectReference();
		this.dateOfConsent = snapshot.getDateOfConsent();
		this.dateOfConsentWithdrawn = snapshot.getDateOfConsentWithdrawn();
		this.deregisterReason = snapshot.getDeregisterReason();
		this.ehrRegistrationStatus = snapshot.getEHRRegistrationStatus();
	}

	public SubjectSnapshot toSnapshot() {
		return SubjectSnapshot.newBuilder()
				.withPatientCDWReference(patientCDWReference)
				.withSubjectId(subjectId)
				.withEdcSubjectReference(edcSubjectReference)
				.withDeregisterReason(deregisterReason)
				.withDateOfConsent(dateOfConsent)
				.withDateOfConsentWithdrawn(dateOfConsentWithdrawn)
				.withEHRRegistrationStatus(ehrRegistrationStatus)
				.build();
	}

	boolean isRegistered() {
		return this.deregisterReason == null && this.dateOfConsentWithdrawn == null;
	}

	boolean sameEdcReference(EDCSubjectReference edcSubjectReference) {
		return this.edcSubjectReference.equals(edcSubjectReference);
	}

	void reregister(Study.SubjectRegistrationData subjectRegistrationData) {
		register(subjectRegistrationData);
	}

	private void register(Study.SubjectRegistrationData subjectRegistrationData) {
		dateOfConsent = subjectRegistrationData.dateOfConsent;
		resetConsentData();
		publishEvent(subjectRegisteredEventFor(subjectRegistrationData.studyId));
	}

	PopulatedEvent populate(PopulationSpecification.Builder populationSpecificationBuilder,
			Function<PopulationSpecification, PopulatedEvent> population) {
		assertReadyForPopulation();
		assertActive();

		return population.apply(addSubjectInformation(populationSpecificationBuilder));
	}

	private void assertActive() {
		if (!isActive()) {
			throw DomainException.of("study.subject.population.failure.wheninactive", subjectId.getId());
		}
	}

	private PopulationSpecification addSubjectInformation(
			PopulationSpecification.Builder populationSpecificationBuilder) {
		return populationSpecificationBuilder.withConsentDate(dateOfConsent)
				.withSubjectId(subjectId)
				.withEdcSubjectReference(edcSubjectReference)
				.withPatientCDWReference(patientCDWReference)
				.build();
	}

	private void resetConsentData() {
		this.dateOfConsentWithdrawn = null;
		this.deregisterReason = null;
	}

	void deregister(Study.DeregisterSubjectData deregisterSubjectData) {
		validate(deregisterSubjectData);
		this.dateOfConsentWithdrawn = deregisterSubjectData.date;
		this.deregisterReason = Optional.ofNullable(deregisterSubjectData.reason)
				.orElse(DeregisterReason.CONSENT_RETRACTED);
		publishEvent(subjectDeregisteredEvent(deregisterSubjectData.studyId));
	}

	@Override
	public boolean isActive() {
		return nonNull(getDateOfConsent()) && isNull(getDateOfConsentWithdrawn());
	}

	private SubjectDeregisteredEvent subjectDeregisteredEvent(StudyId studyId) {
		return SubjectDeregisteredEvent.newBuilder()
				.withReason(deregisterReason)
				.withDate(dateOfConsentWithdrawn)
				.withPatientCDWReference(patientCDWReference)
				.withSubjectId(subjectId)
				.withStudyId(studyId)
				.withDate(dateOfConsentWithdrawn)
				.withReason(deregisterReason)
				.build();
	}

	private SubjectRegisteredEvent subjectRegisteredEventFor(StudyId studyId) {
		return SubjectRegisteredEvent.newBuilder()
				.withPatientId(patientCDWReference.getId())
				.withNamespace(patientCDWReference.getSource())
				.withStudyId(studyId.getId())
				.withSubjectId(subjectId.getId())
				.withDate(dateOfConsent)
				.build();
	}

	@Override
	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	@Override
	public SubjectId getSubjectId() {
		return subjectId;
	}

	@Override
	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference;
	}

	@Override
	public LocalDate getDateOfConsent() {
		return dateOfConsent;
	}

	@Override
	public LocalDate getDateOfConsentWithdrawn() {
		return dateOfConsentWithdrawn;
	}

	@Override
	public DeregisterReason getDeregisterReason() {
		return deregisterReason;
	}

	private void validate(Study.DeregisterSubjectData data) {
		LocalDate deregisterDate = data.date;
		if (deregisterDate.isBefore(dateOfConsent)) {
			throw DomainException.of("study.subject.deregister.date.beforeConsent", Date.from(
					dateOfConsent.atStartOfDay(ZoneId.systemDefault())
							.toInstant()));
		}
		if (deregisterDate.isAfter(toLocaleDate(now()))) {
			throw DomainException.of("study.subject.deregister.date.inFuture");
		}
	}

	private LocalDate toLocaleDate(Instant instant) {
		return instant.atZone(ZoneId.systemDefault())
				.toLocalDate();
	}
}

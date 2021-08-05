package com.custodix.insite.local.ehr2edc;

import static com.custodix.insite.local.ehr2edc.DomainEventPublisher.publishEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.events.SubjectCreated;
import com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.MetaDataDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.InvestigatorsProjection;
import com.custodix.insite.local.ehr2edc.query.SubjectProjection;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.MetaDataDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.user.UserRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class Study {
	private static final String EMPTY_STRING = "";

	private StudyId studyId;
	private String name;
	private String description;

	private MetaDataDefinition metadata;

	private Subjects subjects;
	private Investigators investigators;
	private ItemQueryMappings itemQueryMappings;

	private Study(StudySnapshot entity) {
		studyId = entity.getStudyId();
		name = entity.getName();
		description = entity.getDescription();
		subjects = Subjects.restoreFrom(entity.getSubjects());
		investigators = Investigators.restoreFrom(entity.getInvestigators());
		metadata = MetaDataDefinition.restoreFrom(entity.getMetadata());
		itemQueryMappings = ItemQueryMappings.restoreFrom(entity.getItemQueryMappings());
	}

	private Study(StudyMetaData studyMetaData, StudyRepository studyRepository) {
		validateUniqueStudy(studyMetaData.getId(), studyRepository);
		this.studyId = studyMetaData.getId();
		this.name = studyMetaData.getName();
		this.description = studyMetaData.getDescription();
		this.metadata = studyMetaData.getMetaDataDefinition();
		this.subjects = Subjects.newBuilder()
				.build();
		this.investigators = Investigators.newBuilder()
				.build();
		this.itemQueryMappings = ItemQueryMappings.newBuilder()
				.build();
	}

	public static Study create(StudyMetaData studyMetaData, StudyRepository studyRepository) {
		return new Study(studyMetaData, studyRepository);
	}

	public static Study restoreSnapshot(StudySnapshot entity) {
		return new Study(entity);
	}

	public StudySnapshot toSnapShot() {
		return StudySnapshot.newBuilder()
				.withInvestigators(investigators.toSnapshot())
				.withSubjects(subjects.toSnapshot())
				.withStudyId(studyId)
				.withName(name)
				.withDescription(description)
				.withMetadata(metadataToSnapshot())
				.withItemQueryMappings(itemQueryMappings.toSnapshot())
				.build();
	}

	public void addItemQueryMapping(final ItemDefinitionId itemId, final ItemQueryMappingJson itemQueryMappingJson) {
		this.itemQueryMappings.put(itemId, itemQueryMappingJson);
	}

	public void removeItemQueryMapping(final ItemDefinitionId itemId) {
		this.itemQueryMappings.delete(itemId);
	}

	public void clearItemQueryMappings() {
		this.itemQueryMappings.clear();
	}

	public boolean hasMapping(final ItemDefinitionId itemId) {
		return itemQueryMappings.hasMapping(itemId);
	}

	public ItemQueryMappings getItemQueryMappings() {
		return itemQueryMappings;
	}

	public boolean patientIsNotRegistered(PatientCDWReference patientId) {
		return !this.subjects.isRegistered(patientId);
	}

	public boolean isRegistered(PatientCDWReference patientId) {
		return this.subjects.isRegistered(patientId);
	}

	public EDCSubjectReference getEDCSubjectReference(final SubjectId subjectId) {
		return subjects.findEDCSubjectReference(subjectId)
				.orElseThrow(
						() -> DomainException.of("study.subject.unknownInStudy", studyId.getId(), subjectId.getId()));
	}

	public String eventDefinitionString(PopulatedEvent populatedEvent, Function<Object, String> mapper) {
		return findEventDefinition(populatedEvent.getEventDefinitionId()).map(mapper)
				.orElse(EMPTY_STRING);
	}

	public String itemQueryMappingsString(Function<Object, String> mapper) {
		return mapper.apply(itemQueryMappings);
	}

	public PopulatedEvent populateEvent(EventDefinitionId eventDefinitionId, SubjectId subjectId,
			LocalDate referenceDate, UserIdentifier populator,
			Function<PopulationSpecification, PopulatedEvent> population) {
		assertHasEventDefinitions();

		PopulationSpecification.Builder populationSpecificationBuilder = createPopulationSpecBuilder(referenceDate,
				eventDefinitionId, studyId, populator);
		return subjects.populate(subjectId, populationSpecificationBuilder, population);
	}

	public void updateEHRSubjectRegistrationStatus(SubjectId subjectId, EHRRegistrationStatus status) {
		subjects.updateEHRSubjectRegistrationStatus(subjectId, status);
	}

	private PopulationSpecification.Builder createPopulationSpecBuilder(LocalDate referenceDate,
			EventDefinitionId eventDefinitionId, StudyId studyId, UserIdentifier populator) {
		return PopulationSpecification.newBuilder()
				.withEventDefinition(getEventDefinition(eventDefinitionId))
				.withStudyItemQueryMappings(itemQueryMappings)
				.withPopulator(populator)
				.withStudyId(studyId)
				.withReferenceDate(referenceDate);
	}

	public Subject getSubject(SubjectId subjectId) {
		return subjects.getSubject(subjectId);
	}

	public List<SubjectProjection> getActiveSubjects() {
		return subjects.getActive();
	}

	public SubjectProjection getSubject(PatientCDWReference patientCDWReference) {
		return subjects.getSubject(patientCDWReference);
	}

	public EventPopulationReadiness getEventPopulationReadiness(final SubjectId subjectId) {
		assertHasEventDefinitions();
		return getSubject(subjectId).getEventPopulationReadiness();
	}

	private void assertHasEventDefinitions() {
		if (hasNoEventDefinitions()) {
			throw DomainException.of("study.no.events.present.when.populating.events", studyId.getId());
		}
	}

	private boolean hasNoEventDefinitions() {
		return getMetadata().getEventDefinitions()
				.isEmpty();
	}

	private MetaDataDefinitionSnapshot metadataToSnapshot() {
		return metadata.toSnapshot();
	}

	private void validateUniqueStudy(StudyId studyId, StudyRepository studyRepository) {
		if (studyRepository.exists(studyId)) {
			throw DomainException.getInstance(DomainException.Type.EXISTS, studyId);
		}
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public MetaDataDefinition getMetadata() {
		return metadata;
	}

	public void updateFrom(StudyMetaData metadata) {
		this.name = metadata.getName();
		this.description = metadata.getDescription();
		this.metadata = metadata.getMetaDataDefinition();
	}

	private Optional<EventDefinition> findEventDefinition(EventDefinitionId eventDefinitionId) {
		return metadata.findEventDefinition(eventDefinitionId);
	}

	private EventDefinition getEventDefinition(EventDefinitionId eventDefinitionId) {
		return metadata.findEventDefinition(eventDefinitionId)
				.orElseThrow(() -> DomainException.of("study.eventdefinition.unknown", eventDefinitionId.getId()));
	}

	public Subjects getSubjects() {
		return subjects;
	}

	public void assignInvestigator(UserIdentifier investigatorId, UserRepository userRepository) {
		User user = userRepository.getUser(investigatorId);
		this.investigators.assign(user.getUserIdentifier(), user.getName());
	}

	public void unassignInvestigator(UserIdentifier investigatorId) {
		this.investigators.unassign(investigatorId);
	}

	public InvestigatorsProjection getInvestigators() {
		return investigators;
	}

	public SubjectId register(SubjectRegistrationData subjectRegistrationData, RegisterServices registerServices) {
		validateSubjectReference(subjectRegistrationData.edcSubjectReference, registerServices.edcGateway);

		final SubjectId subjectId = subjects.register(subjectRegistrationData, registerServices);
		publishEvent(toSubjectAddedToStudyEvent(subjectRegistrationData));

		return subjectId;
	}

	private SubjectCreated toSubjectAddedToStudyEvent(SubjectRegistrationData subjectRegistrationData) {
		return SubjectCreated.newBuilder()
				.withStudyId(studyId)
				.withEdcSubjectReference(subjectRegistrationData.edcSubjectReference)
				.build();
	}

	private void validateSubjectReference(EDCSubjectReference edcSubjectReference, EDCStudyGateway edcGateway) {
		boolean validSubjectReference = edcGateway.isRegisteredSubject(studyId, edcSubjectReference)
				.orElse(true);
		if (!validSubjectReference) {
			throw DomainException.of("study.subject.unknown.edc", edcSubjectReference.getId());
		}
	}

	public void deregister(DeregisterSubjectData deregisterSubjectData) {
		subjects.deregister(deregisterSubjectData);
	}

	public boolean hasInvestigatorFor(UserIdentifier userIdentifier) {
		return !investigators.doesNotContain(userIdentifier);
	}

	public boolean hasNoInvestigatorFor(UserIdentifier userIdentifier) {
		return investigators.doesNotContain(userIdentifier);
	}

	public List<EDCSubjectReference> listAvailableSubjectIds(final List<EDCSubjectReference> candidateSubjectIds) {
		return candidateSubjectIds.stream()
				.filter(edcSubjectReference -> !getEdcSubjectReferences().contains(edcSubjectReference))
				.collect(Collectors.toList());
	}

	private List<EDCSubjectReference> getEdcSubjectReferences() {
		return this.subjects.getEdcSubjectReferences();
	}

	public static final class SubjectRegistrationData {
		final PatientCDWReference patientCDWReference;
		final StudyId studyId;
		final EDCSubjectReference edcSubjectReference;
		final LocalDate dateOfConsent;
		final String lastName;
		final String firstName;
		final LocalDate birthDate;

		public SubjectRegistrationData(PatientCDWReference patientCDWReference, StudyId studyId,
				EDCSubjectReference edcSubjectReference, LocalDate dateOfConsent, String lastName, String firstName,
				LocalDate birthDate) {
			this.patientCDWReference = patientCDWReference;
			this.studyId = studyId;
			this.edcSubjectReference = edcSubjectReference;
			this.dateOfConsent = dateOfConsent;
			this.lastName = lastName;
			this.firstName = firstName;
			this.birthDate = birthDate;
		}
	}

	public static final class DeregisterSubjectData {
		final StudyId studyId;
		final SubjectId subjectId;
		final LocalDate date;
		final DeregisterReason reason;

		public DeregisterSubjectData(StudyId studyId, SubjectId subjectId, LocalDate date, DeregisterReason reason) {
			this.studyId = studyId;
			this.subjectId = subjectId;
			this.date = date;
			this.reason = reason;
		}
	}

	public static final class RegisterServices {
		final PatientEHRGateway patientEHRGateway;
		final EDCStudyGateway edcGateway;

		public RegisterServices(PatientEHRGateway patientEHRGateway, EDCStudyGateway edcGateway) {
			this.patientEHRGateway = patientEHRGateway;
			this.edcGateway = edcGateway;
		}
	}
}

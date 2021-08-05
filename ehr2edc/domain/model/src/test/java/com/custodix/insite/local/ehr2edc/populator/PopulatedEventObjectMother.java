package com.custodix.insite.local.ehr2edc.populator;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother.FormKeyObjectMother;
import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public class PopulatedEventObjectMother {
	public static final StudyId STUDY_ID = StudyId.of("123-123");
	public static final SubjectId SUBJECT_ID = SubjectId.of("123-456");
	public static final EventDefinitionId EVENT_DEFINITION_ID = EventDefinitionId.of("1234-123");
	public static final EventId EVENT_ID = EventId.of("form-123");
	public static final LocalDate REFERENCE_DATE = LocalDate.now();
	public static final EDCSubjectReference EDC_SUBJECT_REFERENCE = EDCSubjectReference.of("Form.EDCSubjectReference");
	public static final UserIdentifier POPULATOR = UserIdentifier.of("populator");

	public static PopulatedEvent aDefaultEvent() {
		return aDefaultEventBuilder().build();
	}

	public static PopulatedEvent.Builder aDefaultEventBuilder() {
		return aDefaultEventBuilder(STUDY_ID, SUBJECT_ID, EVENT_DEFINITION_ID);
	}

	public static PopulatedEvent.Builder aDefaultEventBuilder(StudyId studyId, SubjectId subjectId) {
		return aDefaultEventBuilder(studyId, subjectId, EVENT_DEFINITION_ID);
	}

	public static PopulatedEvent.Builder aDefaultEventBuilder(StudyId studyId, SubjectId subjectId,
			EventDefinitionId eventDefinitionId) {
		return PopulatedEvent.newBuilder()
				.withInstanceId(EVENT_ID)
				.withSubjectId(subjectId)
				.withStudyId(studyId)
				.withReferenceDate(REFERENCE_DATE)
				.withEventDefinitionId(eventDefinitionId)
				.withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
				.withForms(Collections.singletonList(PopulatedFormObjectMother.aDefaultForm()))
				.withPopulator(POPULATOR);
	}

	public static PopulatedEvent generateEvent(StudyId studyId, EventDefinitionId eventDefinitionId, SubjectId subjectId) {
		return getEventBuilder(studyId, eventDefinitionId, subjectId)
				.build();
	}

	private static PopulatedEvent.Builder getEventBuilder(StudyId studyId, EventDefinitionId eventDefinitionId,
			SubjectId subjectId) {
		return PopulatedEvent.newBuilder()
				.withInstanceId(EventId.of(randomUUID().toString()))
				.withStudyId(studyId)
				.withEdcSubjectReference(EDCSubjectReference.of(randomUUID().toString()))
				.withEventDefinitionId(eventDefinitionId)
				.withSubjectId(subjectId)
				.withReferenceDate(LocalDate.of(2016, 6, 9))
				.withForms(Collections.singletonList(PopulatedFormObjectMother.generateForm()))
				.withPopulationTime(Instant.parse("2019-07-01T08:00:00Z"))
				.withPopulator(POPULATOR);
	}

	public static PopulatedEvent generateEvent(StudyId studyId, EventDefinitionId eventDefinitionId, String eventParentId,
			SubjectId subjectId) {
		return PopulatedEvent.newBuilder()
				.withInstanceId(EventId.of(randomUUID().toString()))
				.withStudyId(studyId)
				.withEdcSubjectReference(EDCSubjectReference.of(randomUUID().toString()))
				.withEventDefinitionId(eventDefinitionId)
				.withEventParentId(eventParentId)
				.withSubjectId(subjectId)
				.withReferenceDate(LocalDate.of(2016, 6, 9))
				.withForms(Collections.singletonList(PopulatedFormObjectMother.aDefaultForm()))
				.withPopulator(POPULATOR)
				.build();
	}

	public static List<PopulatedEvent> createForAllSubjectsAndForms(StudySnapshot studySnapshot) {
		List<FormKeyObjectMother> formKeys = getFormKeys(studySnapshot);
		Collection<SubjectSnapshot> subjects = studySnapshot.getSubjects();
		return subjects.stream()
				.map(subject -> {
					List<PopulatedForm> populatedForms = formKeys.stream()
							.map(PopulatedFormObjectMother::createForm)
							.collect(toList());
					return PopulatedEventObjectMother.createEvent(populatedForms, formKeys.get(0).studyId,
							formKeys.get(0).eventDefinitionId, subject, POPULATOR);
				})
				.collect(toList());
	}

	public static List<PopulatedEvent> createForAllSubjectsAndFormsNoPopulator(StudySnapshot studySnapshot) {
		List<FormKeyObjectMother> formKeys = getFormKeys(studySnapshot);
		Collection<SubjectSnapshot> subjects = studySnapshot.getSubjects();
		return subjects.stream()
				.map(subject -> {
					List<PopulatedForm> populatedForms = formKeys.stream()
							.map(PopulatedFormObjectMother::createForm)
							.collect(toList());
					return PopulatedEventObjectMother.createEvent(populatedForms, formKeys.get(0).studyId,
							formKeys.get(0).eventDefinitionId, subject, null);
				})
				.collect(toList());
	}

	private static List<FormKeyObjectMother> getFormKeys(StudySnapshot studySnapshot) {
		return studySnapshot.getMetadata()
				.getEventDefinitions()
				.stream()
				.flatMap(event -> createFormKey(studySnapshot, event))
				.collect(toList());
	}

	private static Stream<FormKeyObjectMother> createFormKey(StudySnapshot studySnapshot,
			EventDefinitionSnapshot event) {
		return event.getFormDefinitionSnapshots()
				.stream()
				.map(form -> new FormKeyObjectMother(studySnapshot.getStudyId(), event.getId(), form));
	}

	private static PopulatedEvent createEvent(List<PopulatedForm> populatedForms, StudyId studyId, EventDefinitionId eventDefinitionId,
			SubjectSnapshot subject, UserIdentifier populator) {
		return PopulatedEvent.newBuilder()
				.withInstanceId(EventId.of(randomUUID().toString()))
				.withStudyId(studyId)
				.withEventDefinitionId(eventDefinitionId)
				.withSubjectId(subject.getSubjectId())
				.withEdcSubjectReference(subject.getEdcSubjectReference())
				.withReferenceDate(LocalDate.of(2016, 6, 9))
				.withForms(populatedForms)
				.withPopulationTime(Instant.parse("2018-12-12T13:10:12Z"))
				.withPopulator(populator)
				.build();
	}
}
package com.custodix.insite.local.ehr2edc.submitted;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.DomainTime;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class SubmittedEventObjectMother {

	public static final SubjectId SUBJECT_ID = SubjectId.of("subjectId");

	public static SubmittedEvent aDefaultSubmittedEvent() {
		return aDefaultSubmittedEventBuilder().build();
	}

	public static SubmittedEvent.Builder aDefaultSubmittedEventBuilder() {
		return SubmittedEvent.newBuilder()
				.withId(SubmittedEventId.of("SubmittedEventId"))
				.withPopulatedForms(Collections.singletonList(PopulatedFormObjectMother.aDefaultFormBuilder()
						.build()))
				.withSubmitter(UserIdentifier.of("UserId"))
				.withSubmittedDate(Instant.now())
				.withEventDefinitionId(EventDefinitionId.of("eventDefinitionId"))
				.withEventParentId("eventParentId")
				.withStudyId(StudyId.of("studyId"))
				.withSubjectId(SUBJECT_ID)
				.withSubmittedForms(Collections.singletonList(SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
						.build()))
				.withPopulatedEventId(EventId.of("populatedEventId"));
	}

	public static SubmittedEvent aSubmittedEvent(PopulatedEvent populatedEvent, UserIdentifier submitter) {
		return SubmittedEvent.newBuilder()
				.withId(SubmittedEventId.of("${json-unit.ignore}"))
				.withEventDefinitionId(populatedEvent.getEventDefinitionId())
				.withEventParentId(populatedEvent.getEventParentId())
				.withPopulatedEventId(populatedEvent.getInstanceId())
				.withPopulatedForms(populatedEvent.getPopulatedForms())
				.withPopulator(populatedEvent.getPopulator()
						.get())
				.withPopulationTime(populatedEvent.getPopulationTime())
				.withReferenceDate(populatedEvent.getReferenceDate())
				.withStudyId(populatedEvent.getStudyId())
				.withSubjectId(populatedEvent.getSubjectId())
				.withSubmittedDate(DomainTime.now())
				.withSubmitter(submitter)
				.withSubmittedForms(populatedEvent.getPopulatedForms()
						.stream()
						.map(SubmittedFormObjectMother::aSubmittedForm)
						.collect(Collectors.toList()))
				.build();
	}
}
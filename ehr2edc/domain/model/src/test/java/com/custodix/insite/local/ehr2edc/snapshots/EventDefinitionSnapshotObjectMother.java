package com.custodix.insite.local.ehr2edc.snapshots;

import static com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshotObjectMother.generateFormDefinition;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.stream.IntStream;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;

public class EventDefinitionSnapshotObjectMother {

	private static final EventDefinitionId EVENT_ID = EventDefinitionId.of("eventDefinitionId");
	private static final String EVENT_NAME = "Event Name";

	public static EventDefinitionSnapshot aDefaultEventDefinitionSnapshot() {
		return aDefaultEventDefinitionSnapshotBuilder().build();
	}

	public static EventDefinitionSnapshot.Builder aDefaultEventDefinitionSnapshotBuilder() {
		return EventDefinitionSnapshot.newBuilder()
				.withId(EventDefinitionId.of("EMPTY"))
				.withFormDefinitions(Collections.singletonList(FormDefinitionSnapshotObjectMother.aDefaultFormDefinitionSnapshot()));

	}

	public static EventDefinitionSnapshot eventDefinitionWithoutForms() {
		return EventDefinitionSnapshot.newBuilder()
				.withId(EventDefinitionId.of("EMPTY"))
				.withFormDefinitions(Collections.emptyList())
				.build();

	}

	public static EventDefinitionSnapshot eventDefinitionWithSingleForm() {
		FormDefinitionSnapshot form = FormDefinitionSnapshotObjectMother.formDefinitionSnapshot();
		return EventDefinitionSnapshot.newBuilder()
				.withId(EventDefinitionId.of("SCREENING"))
				.withFormDefinitions(Collections.singletonList(form))
				.build();
	}

	public static EventDefinitionSnapshot generateEventDefinition() {
		return generateEventDefinition(EVENT_ID);
	}

	public static EventDefinitionSnapshot generateEventDefinition(EventDefinitionId eventDefinitionId) {
		return EventDefinitionSnapshot.newBuilder()
				.withId(eventDefinitionId)
				.withName(EVENT_NAME)
				.withFormDefinitions(IntStream.range(0, 5)
						.mapToObj(i -> generateFormDefinition(i))
						.collect(toList()))
				.build();
	}

	public static EventDefinitionSnapshot generateEventDefinitionWithNoFormDefinitions() {
		return EventDefinitionSnapshot.newBuilder()
				.withId(EventDefinitionId.of("eventDefinitionId"))
				.withName("Event Name")
				.withFormDefinitions(null)
				.build();
	}
}

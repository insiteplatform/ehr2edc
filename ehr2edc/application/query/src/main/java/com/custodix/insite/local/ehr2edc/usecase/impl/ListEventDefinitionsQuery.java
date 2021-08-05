package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.collectingAndThen;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.ListEventDefinitions;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Query
class ListEventDefinitionsQuery implements ListEventDefinitions {

	private final StudyRepository studyRepository;
	private final PopulatedEventRepository populatedEventRepository;
	private final SubmittedEventRepository submittedEventRepository;

	ListEventDefinitionsQuery(StudyRepository studyRepository, PopulatedEventRepository populatedEventRepository,
			SubmittedEventRepository submittedEventRepository) {
		this.studyRepository = studyRepository;
		this.populatedEventRepository = populatedEventRepository;
		this.submittedEventRepository = submittedEventRepository;
	}

	@Override
	public Response list(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		return study.getMetadata()
				.getEventDefinitions()
				.stream()
				.map(e -> toEventDefinition(study, request, e))
				.filter(e -> e.getQueryCount() != 0)
				.collect(collectingAndThen(Collectors.toList(), this::toResponse));
	}

	private Response toResponse(List<EventDefinition> eventDefinitions) {
		return Response.newBuilder()
				.withEventDefinitionsInStudy(eventDefinitions)
				.build();
	}

	private EventDefinition toEventDefinition(Study study, Request request,
			com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition definition) {
		EventDefinition.Builder builder = EventDefinition.newBuilder()
				.withEventDefinitionId(definition.getId())
				.withName(definition.getName())
				.withFormDefinitions(mapFormDefinitions(study, definition))
				.withQueryCount(getQueryCount(study, definition))
				.withHistoryAvailable(isHistoryAvailable(request.getSubjectId(), definition.getId()));
		populatedEventRepository.findLatestEvent(request.getSubjectId(), definition.getId())
				.ifPresent(e -> enrichEventDefinition(e, builder));
		return builder.build();
	}

	private boolean isHistoryAvailable(SubjectId subjectId, EventDefinitionId eventDefinitionId) {
		return submittedEventRepository.findMostRecentSubmittedEvent(subjectId, eventDefinitionId)
				.isPresent();
	}

	private void enrichEventDefinition(PopulatedEvent e, EventDefinition.Builder builder) {
		builder.withEventId(e.getInstanceId())
				.withLastPopulationTime(e.getPopulationTime())
				.withLastReferenceDate(e.getReferenceDate());
	}

	private List<FormDefinition> mapFormDefinitions(Study study,
			com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition eventDefinition) {
		List<FormDefinition> formDefinitions = Collections.emptyList();
		if (hasFormDefinitions(eventDefinition)) {
			formDefinitions = eventDefinition.getFormDefinitions()
					.stream()
					.map(f -> mapFormDefinition(study, f))
					.collect(Collectors.toList());
		}
		return formDefinitions;
	}

	private FormDefinition mapFormDefinition(Study study,
			com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition formDefinition) {
		return FormDefinition.newBuilder()
				.withFormDefinitionId(formDefinition.getId())
				.withName(formDefinition.getName())
				.withQueryCount(getQueryCount(study, formDefinition))
				.build();
	}

	private long getQueryCount(Study study,
			com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition eventDefinition) {
		if (hasFormDefinitions(eventDefinition)) {
			return eventDefinition.getFormDefinitions()
					.stream()
					.mapToLong(f -> getQueryCount(study, f))
					.sum();
		}
		return 0L;
	}

	private long getQueryCount(Study study,
			com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition formDefinition) {
		return getItems(formDefinition).map(ItemDefinition::getId)
				.filter(study::hasMapping)
				.count();
	}

	private boolean hasFormDefinitions(com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition definition) {
		return definition.getFormDefinitions() != null;
	}

	private Stream<ItemDefinition> getItems(
			com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition formDefinition) {
		return formDefinition.getItemGroupDefinitions()
				.stream()
				.flatMap(group -> group.getItemDefinitions()
						.stream());
	}
}
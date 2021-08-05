package com.custodix.insite.local.ehr2edc.query.populator;

import static com.custodix.insite.local.ehr2edc.DomainTime.now;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition;
import com.custodix.insite.local.ehr2edc.populator.*;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;

@Component
class EventPopulatorImpl implements EventPopulator {
	private final QueryMappingFormPopulator formPopulator;

	EventPopulatorImpl(QueryMappingFormPopulator formPopulator) {
		this.formPopulator = formPopulator;
	}

	@Override
	public PopulatedEvent populateEvent(PopulationSpecification populationSpecification) {
		List<PopulatedForm> populatedForms = populationSpecification.getEventDefinition()
				.getFormDefinitions()
				.stream()
				.map(form -> formPopulator.populateForm(populationSpecification, form))
				.filter(PopulatedForm::hasItems)
				.collect(Collectors.toList());
		return buildEvent(populationSpecification, populatedForms);
	}

	private PopulatedEvent buildEvent(PopulationSpecification populationSpecification,
			List<PopulatedForm> populatedForms) {
		EventDefinition eventDefinition = populationSpecification.getEventDefinition();
		return PopulatedEvent.newBuilder()
				.withEdcSubjectReference(populationSpecification.getEdcSubjectReference())
				.withEventDefinitionId(eventDefinition.getId())
				.withForms(populatedForms)
				.withReferenceDate(populationSpecification.getReferenceDate())
				.withPopulationTime(now())
				.withInstanceId(EventId.generate())
				.withEventParentId(eventDefinition.getParentId())
				.withSubjectId(populationSpecification.getSubjectId())
				.withName(eventDefinition.getName())
				.withStudyId(populationSpecification.getStudyId())
				.withPopulator(populationSpecification.getPopulator())
				.build();
	}
}

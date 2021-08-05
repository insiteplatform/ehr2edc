package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.audit.PopulationContext;
import com.custodix.insite.local.ehr2edc.audit.PopulationContextRepository;
import com.custodix.insite.local.ehr2edc.domain.service.ObjectToStringMappingService;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.vocabulary.PopulationContextId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Component
class PopulationAuditing {
	private final PopulationContextRepository populationContextRepository;
	private final ObjectToStringMappingService toStringMapper;
	private final List<DataPointEHRGateway> dataPointEHRGateways;

	PopulationAuditing(PopulationContextRepository populationContextRepository,
			ObjectToStringMappingService toStringMapper, List<DataPointEHRGateway> dataPointEHRGateways) {
		this.populationContextRepository = populationContextRepository;
		this.toStringMapper = toStringMapper;
		this.dataPointEHRGateways = dataPointEHRGateways;
	}

	private List<String> retrieveDataPointsFor(SubjectId subjectId) {
		return dataPointEHRGateways.stream()
				.map(gateway -> gateway.findAllForSubject(subjectId))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private PopulationContext buildPopulationContext(SubjectId subjectId, Study study, PopulatedEvent populatedEvent) {
		return PopulationContext.newBuilder()
				.withId(PopulationContextId.generate())
				.withEventId(populatedEvent.getInstanceId())
				.withEventDefinitionJson(study.eventDefinitionString(populatedEvent, toStringMapper::map))
				.withItemQueryMappingsJson(study.itemQueryMappingsString(toStringMapper::map))
				.withDatapointsJsons(retrieveDataPointsFor(subjectId))
				.build();
	}

	void createPopulationContext(SubjectId subjectId, Study study, PopulatedEvent populatedEvent) {
		PopulationContext populationContext = buildPopulationContext(subjectId, study, populatedEvent);
		populationContextRepository.save(populationContext);
	}
}

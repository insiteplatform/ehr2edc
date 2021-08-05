package com.custodix.insite.local.ehr2edc.populator;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.util.List;
import java.util.Optional;

public interface PopulatedEventRepository {

	void save(PopulatedEvent populatedEvent);

	Optional<PopulatedEvent> findLatestEvent(SubjectId subjectId, EventDefinitionId eventDefinitionId);

	PopulatedEvent getEvent(EventId eventId);

	List<PopulatedEvent> findBy(SubjectId subjectId, EventDefinitionId eventDefinitionId);
}
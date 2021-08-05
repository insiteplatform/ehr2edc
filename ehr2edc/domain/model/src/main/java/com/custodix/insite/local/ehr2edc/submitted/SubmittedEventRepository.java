package com.custodix.insite.local.ehr2edc.submitted;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;

public interface SubmittedEventRepository {
	void save(SubmittedEvent submittedEvent);

	Optional<SubmittedEvent> findMostRecentSubmittedEvent(SubjectId subjectId, EventDefinitionId eventDefinitionId);

	List<SubmittedEvent> findAllSubmittedEvents(SubjectId subjectId, EventDefinitionId eventDefinitionId);

	Optional<SubmittedEvent> findById(SubmittedEventId submittedEventId);

	SubmittedEvent get(SubmittedEventId submittedEventId);
}
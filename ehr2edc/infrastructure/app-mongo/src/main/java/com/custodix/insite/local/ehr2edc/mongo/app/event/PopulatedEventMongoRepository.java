package com.custodix.insite.local.ehr2edc.mongo.app.event;

import com.custodix.insite.local.ehr2edc.mongo.app.document.EventMongoDocument;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class PopulatedEventMongoRepository implements PopulatedEventRepository {

	private final EventMongoSnapshotRepository eventMongoSnapshotRepository;

	public PopulatedEventMongoRepository(final EventMongoSnapshotRepository eventMongoSnapshotRepository) {
		this.eventMongoSnapshotRepository = eventMongoSnapshotRepository;
	}

	@Override
	public void save(final PopulatedEvent populatedEvent) {
		eventMongoSnapshotRepository.save(EventMongoDocument.from(populatedEvent));
	}

	@Override
	public Optional<PopulatedEvent> findLatestEvent(final SubjectId subjectId, final EventDefinitionId eventDefinitionId) {
		return eventMongoSnapshotRepository.findFirstBySubjectIdAndEventDefinitionIdOrderByPopulationTimeDesc(
				subjectId.getId(), eventDefinitionId.getId())
				.map(EventMongoDocument::toEvent);
	}

	@Override
	public PopulatedEvent getEvent(EventId eventId) {
		return eventMongoSnapshotRepository.findByInstanceId(eventId.getId())
				.orElseThrow(() -> DomainException.getInstance(DomainException.Type.NOT_EXISTS, eventId))
				.toEvent();
	}

	@Override
	public List<PopulatedEvent> findBy(SubjectId subjectId, EventDefinitionId eventDefinitionId) {
		return eventMongoSnapshotRepository.findBySubjectIdAndEventDefinitionIdOrderByPopulationTimeDesc(
				subjectId.getId(), eventDefinitionId.getId())
				.stream()
				.map(EventMongoDocument::toEvent)
				.collect(toList());
	}

	@Repository
	interface EventMongoSnapshotRepository extends MongoRepository<EventMongoDocument, String> {
		Optional<EventMongoDocument> findFirstBySubjectIdAndEventDefinitionIdOrderByPopulationTimeDesc(
				final String subjectId, final String eventDefinitionId);

		List<EventMongoDocument> findBySubjectIdAndEventDefinitionIdOrderByPopulationTimeDesc(final String subjectId,
				final String eventDefinitionId);

		Optional<EventMongoDocument> findByFormsInstanceIdIn(String instanceId);

		Optional<EventMongoDocument> findByInstanceId(String instanceId);
	}
}
package com.custodix.insite.local.ehr2edc.mongo.app.submitted;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContext;
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContextRepository;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;

@Repository
class SubmittedEventMongoRepository implements SubmittedEventRepository, SubmissionContextRepository {

	private final AuditedReviewSnapshotRepository auditedReviewSnapshotRepository;
	private final ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory;

	SubmittedEventMongoRepository(AuditedReviewSnapshotRepository auditedReviewSnapshotRepository,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		this.auditedReviewSnapshotRepository = auditedReviewSnapshotRepository;
		this.reviewedProvenanceDataPointDocumentFactory = reviewedProvenanceDataPointDocumentFactory;
	}

	@Override
	public void save(SubmittedEvent submittedEvent) {
		auditedReviewSnapshotRepository.save(
				ReviewContextDocument.create(submittedEvent, reviewedProvenanceDataPointDocumentFactory));
	}

	@Override
	public Optional<SubmittedEvent> findMostRecentSubmittedEvent(SubjectId subjectId,
			EventDefinitionId eventDefinitionId) {
		return auditedReviewSnapshotRepository.findFirstByReviewedEventSubjectIdAndReviewedEventEventDefinitionIdOrderByReviewDateDesc(
				subjectId.getId(), eventDefinitionId.getId())
				.map(ReviewContextDocument::toSubmittedEvent);
	}

	@Override
	public List<SubmittedEvent> findAllSubmittedEvents(SubjectId subjectId, EventDefinitionId eventDefinitionId) {
		return auditedReviewSnapshotRepository.findByReviewedEventSubjectIdAndReviewedEventEventDefinitionIdOrderByReviewDateDesc(
				subjectId.getId(), eventDefinitionId.getId())
				.stream()
				.map(ReviewContextDocument::toSubmittedEvent)
				.collect(toList());
	}

	@Override
	public Optional<SubmittedEvent> findById(SubmittedEventId submittedEventId) {
		return auditedReviewSnapshotRepository.findById(submittedEventId.getId())
				.map(ReviewContextDocument::toSubmittedEvent);
	}

	@Override
	public SubmittedEvent get(SubmittedEventId submittedEventId) {
		return findById(submittedEventId).orElseThrow(
				() -> DomainException.of("study.submitted.event.unknown", submittedEventId.getId()));
	}

	@Override
	public void save(SubmissionContext submissionContext) {
		ReviewContextDocument updateReviewContextDocument = ReviewContextDocument.newBuilder(
				getReviewContextDocument(submissionContext))
				.withSubmittedXml(submissionContext.getSubmittedXml())
				.build();

		auditedReviewSnapshotRepository.save(updateReviewContextDocument);
	}

	private ReviewContextDocument getReviewContextDocument(SubmissionContext submissionContext) {
		return auditedReviewSnapshotRepository.findById(submissionContext.getSubmittedEventId()
				.getId())
				.orElseThrow(() -> DomainException.of("study.submitted.event.unknown",
						submissionContext.getSubmittedEventId()
								.getId()));
	}

	@Repository
	interface AuditedReviewSnapshotRepository extends MongoRepository<ReviewContextDocument, String> {
		Optional<ReviewContextDocument> findFirstByReviewedEventSubjectIdAndReviewedEventEventDefinitionIdOrderByReviewDateDesc(
				String subjectId, String eventDefinitionId);

		List<ReviewContextDocument> findByReviewedEventSubjectIdAndReviewedEventEventDefinitionIdOrderByReviewDateDesc(
				String subjectId, String eventDefinitionId);

		Optional<ReviewContextDocument> findById(String id);
	}
}
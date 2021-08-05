package com.custodix.insite.local.ehr2edc.mongo.app.submitted

import com.custodix.insite.local.ehr2edc.mongo.app.AppMongoTestContext
import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument
import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocumentObjectMother
import com.custodix.insite.local.ehr2edc.mongo.app.review.*
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContext
import com.custodix.insite.local.ehr2edc.submitted.SubmissionContextRepository
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static org.junit.Assert.assertEquals

@SpringBootTest(classes = AppMongoTestContext.class)
class SubmissionContextRepositorySpec extends Specification {

	public static final String SUBMITTED_XML = "<myXml />"
	@Autowired
	private SubmissionContextRepository submissionContextRepository;

	@Autowired
	private SubmittedEventMongoRepository.AuditedReviewSnapshotRepository auditedReviewSnapshotRepository;

	def 'Repository stores submitted event correctly'() {
		given: "a existing reviewContextDocument without submoitted xml"
        ReviewContextDocument originalReviewContextDocument = aReviewContextWithoutSubmittedXml()
		and: "a submission context"
		def submissionContext = SubmissionContext.newBuilder()
				.withSubmittedXml(SUBMITTED_XML)
				.withSubmittedEventId(SubmittedEventId.of(originalReviewContextDocument.getId()))
				.build()

		when: "saving submitted event"
		submissionContextRepository.save(submissionContext);
		ReviewContextDocument savedReviewContextDocument = auditedReviewSnapshotRepository.findById(originalReviewContextDocument.getId())
				.orElseThrow{new AssertionError()}

		then: "submitted event  is saved correctly"
		savedReviewContextDocument.getSubmittedXml() == SUBMITTED_XML
		assertReviewedEvent(originalReviewContextDocument, savedReviewContextDocument)
		assertReviewedForm(originalReviewContextDocument, savedReviewContextDocument)
		assertReviewedItemGroup(originalReviewContextDocument, savedReviewContextDocument)
		assertReviewedItem(originalReviewContextDocument, savedReviewContextDocument)
	}

    private ReviewContextDocument aReviewContextWithoutSubmittedXml() {
		def submittedEventId = "submitted-event-id-098"
		def originalReviewContextDocument = ReviewContextDocumentObjectMother.aDefaultReviewContextDocumentBuilder()
				.withId(submittedEventId)
				.withSubmittedXml(null)
				.build()
		def savedReviewContextDocument = auditedReviewSnapshotRepository.save(originalReviewContextDocument)
		return savedReviewContextDocument
	}

	private void assertReviewedEvent(ReviewContextDocument original , ReviewContextDocument saved) {
		assertEquals(original.getId(), saved.getId())
		assertEquals(original.getReviewDate(), saved.getReviewDate())
		assertEquals(original.getReviewerUserId(), saved.getReviewerUserId())
		assertEquals(original.getReviewedEvent().getStudyId(), saved.getReviewedEvent().getStudyId())
		assertEquals(original.getReviewedEvent().getSubjectId(), saved.getReviewedEvent().getSubjectId());
		assertEquals(original.getReviewedEvent().getEventDefinitionId(), saved.getReviewedEvent().getEventDefinitionId());
		assertEquals(original.getReviewedEvent().getEventParentId(),  saved.getReviewedEvent().getEventParentId());
		assertEquals(original.getReviewedEvent().getPopulatedEventId(), saved.getReviewedEvent().getPopulatedEventId());
	}

	private void assertReviewedForm(ReviewContextDocument original,  ReviewContextDocument saved) {
		ReviewedFormMongoSnapshot savedForm = getReviewedForm(saved)
		ReviewedFormMongoSnapshot originalForm = getReviewedForm(original)
		assertEquals(originalForm.getName(), savedForm.getName());
		assertEquals(originalForm.getFormDefinitionId(), savedForm.getFormDefinitionId())
		assertEquals(originalForm.getPopulatedFormId(), savedForm.getPopulatedFormId())
	}

	private ReviewedItemGroupMongoSnapshot assertReviewedItemGroup(ReviewContextDocument original,
																   ReviewContextDocument saved) {
		ReviewedItemGroupMongoSnapshot savedItemGroup = getReviewedForm(saved)
				.getReviewedItemGroups()
				.get(0);
		ReviewedItemGroupMongoSnapshot originalItemGroup = getReviewedForm(original)
				.getReviewedItemGroups()
				.get(0);
		assertEquals(originalItemGroup.getId(), savedItemGroup.getId());
		assertEquals(originalItemGroup.isRepeating(), savedItemGroup.isRepeating());
		assertEquals(originalItemGroup.getPopulatedItemGroupId(), savedItemGroup.getPopulatedItemGroupId());
		return savedItemGroup;
	}

	private void assertReviewedItem(ReviewContextDocument original, ReviewContextDocument saved) {
		ReviewedItemMongoSnapshot savedItem = getReviewedForm(saved)
				.getReviewedItemGroups()
				.get(0)
				.getReviewedItems()
				.get(0)
		ReviewedItemMongoSnapshot originalItem = getReviewedForm(original)
				.getReviewedItemGroups()
				.get(0)
				.getReviewedItems()
				.get(0)
		assertEquals(originalItem.getId(), savedItem.getId());
		assertEquals(originalItem.isKey(), savedItem.isKey());
		assertEquals(originalItem.getPopulatedItemId(), savedItem.getPopulatedItemId());
		assertLabeledValue(originalItem.getLabeledValue(), savedItem.getLabeledValue())
	}

	private void assertLabeledValue(final ReviewedLabeledValueDocument original, final ReviewedLabeledValueDocument saved) {
		assertEquals(original.getValue(), saved.getValue());
		assertEquals( 1, saved.getLabels().size());
		assertLabel(original.getLabels().get(0), saved.getLabels().get(0));
	}

	private void assertLabel(final ReviewedLabelDocument original, final ReviewedLabelDocument saved) {
		assertEquals(original.getText(),saved.getText() );
		assertEquals(original.getLocale(),saved.getLocale() );
	}

	private ReviewedFormMongoSnapshot getReviewedForm(ReviewContextDocument document) {
		return document.getReviewedEvent()
				.getReviewedForms()
				.stream()
				.findFirst().orElseThrow { new AssertionError() }
	}
}
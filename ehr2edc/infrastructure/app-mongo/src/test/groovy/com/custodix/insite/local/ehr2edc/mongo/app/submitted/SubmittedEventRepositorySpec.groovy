package com.custodix.insite.local.ehr2edc.mongo.app.submitted

import com.custodix.insite.local.ehr2edc.mongo.app.AppMongoTestContext
import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument
import com.custodix.insite.local.ehr2edc.mongo.app.review.*
import com.custodix.insite.local.ehr2edc.submitted.*
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.google.common.collect.ImmutableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.Instant

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedLabelObjectMother.aDefaultSubmittedLabelBuilder
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

@SpringBootTest(classes = AppMongoTestContext.class)
class SubmittedEventRepositorySpec extends Specification {

	@Autowired
	private SubmittedEventRepository submittedEventRepository;

	@Autowired
	private SubmittedEventMongoRepository.AuditedReviewSnapshotRepository auditedReviewSnapshotRepository;

	def 'Repository stores submitted event correctly'() {
		given: "a submittedEvent "
		SubmittedItem submittedItem = submittedItem();
		SubmittedItemGroup submittedItemGroup = submittedItemGroupContaining(submittedItem);
		SubmittedForm submittedForm = submittedFormContaining(submittedItemGroup);
		SubmittedEvent submittedEvent = submittedEventContaining(submittedForm);

		when: "saving submitted event"
		submittedEventRepository.save(submittedEvent);
		ReviewContextDocument snapshot = auditedReviewSnapshotRepository.findById(submittedEvent.getId()
				.getId())
				.orElseThrow{new AssertionError()}

		then: "submitted event  is saved correctly"
		assertReviewedEvent(submittedEvent, snapshot);
		assertReviewedForm(submittedForm, snapshot);
		assertReviewedItemGroup(submittedItemGroup, snapshot);
		assertReviewedItem(submittedItem, snapshot);
	}

	private SubmittedItem submittedItem() {
		String submittedItemId = "test-submittedItem-id";
		SubmittedLabeledValue value = SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder()
				.withValue("test-value")
				.withLabels(ImmutableList.of(aDefaultSubmittedLabelBuilder().withText("test-text-label")
						.withLocale(Locale.KOREAN)
						.build()))
				.build();
		return SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
				.withId(ItemDefinitionId.of(submittedItemId))
				.withValue(value)
				.withPopulatedItemId(ItemId.of("populatedItemId"))
				.build();
	}

	private SubmittedItemGroup submittedItemGroupContaining(SubmittedItem submittedItem) {
		String reviewedItemGroupId = "test-reviewedItemGroup-id";
		boolean repeating = true;
		return SubmittedItemGroup.newBuilder()
				.withId(ItemGroupDefinitionId.of(reviewedItemGroupId))
				.withName("itemGroupName")
				.withSubmittedItems(singletonList(submittedItem))
				.withRepeating(repeating)
				.withPopulatedItemGroupId(ItemGroupId.of("populatedItemGroupId"))
				.build();
	}

	private SubmittedForm submittedFormContaining(SubmittedItemGroup submittedItemGroup) {
		FormDefinitionId formDefinitionId = FormDefinitionId.of("test-form-id");
		return SubmittedForm.newBuilder()
				.withName("test-form-name")
				.withFormDefinitionId(formDefinitionId)
				.withSubmittedItemGroups(singletonList(submittedItemGroup))
				.withPopulatedFormId(FormId.of("populatedFormId"))
				.build();
	}

	private SubmittedEvent submittedEventContaining(SubmittedForm submittedForm) {
		StudyId studyId = StudyId.of("test-study-id");
		SubjectId subjectId = SubjectId.generate();
		Instant now = Instant.now();
		String xml = "xml";
		SubmittedEvent reviewedEvent = SubmittedEvent.newBuilder()
				.withId(SubmittedEventId.newId())
				.withStudyId(studyId)
				.withSubjectId(subjectId)
				.withSubmittedForms(singletonList(submittedForm))
				.withEventDefinitionId(EventDefinitionId.of("test-evtdef-id"))
				.withEventParentId("test-parent-id")
				.withPopulatedEventId(EventId.of("populatedEventId"))
				.withSubmittedDate(now)
				.withPopulatedForms(emptyList())
				.withSubmitter(UserIdentifier.of("test-user"))
				.withPopulator(UserIdentifier.of("populator"))
				.build();
		return reviewedEvent;
	}

	private void assertReviewedEvent(SubmittedEvent reviewedEvent, ReviewContextDocument snapshot) {
		assertEquals(reviewedEvent.getId()
				.getId(), snapshot.getId());
		assertTrue(snapshot.getPopulatedForms()
				.isEmpty());
		assertEquals(reviewedEvent.getSubmittedDate(), snapshot.getReviewDate());
		assertEquals(reviewedEvent.getSubmitter(), UserIdentifier.of(snapshot.getReviewerUserId()));
		ReviewedEventDocument reviewedEventSnapshot = snapshot.getReviewedEvent();
		assertEquals(reviewedEvent.getStudyId()
				.getId(), reviewedEventSnapshot.getStudyId());
		assertEquals(reviewedEvent.getSubjectId()
				.getId(), reviewedEventSnapshot.getSubjectId());
		assertEquals(reviewedEvent.getEventDefinitionId()
				.getId(), reviewedEventSnapshot.getEventDefinitionId());
		assertEquals(reviewedEvent.getEventParentId(), reviewedEventSnapshot.getEventParentId());
		assertEquals(reviewedEvent.getPopulatedEventId()
				.getId(), reviewedEventSnapshot.getPopulatedEventId());
		assertEquals(UserIdentifier.of("populator"), reviewedEvent.populator.get())
	}

	private void assertReviewedForm(SubmittedForm reviewedForm, ReviewContextDocument snapshot) {
		ReviewedFormMongoSnapshot form = findReviewedForm(snapshot).orElseThrow{new AssertionError()}
		assertEquals(reviewedForm.getName(), form.getName());
		assertEquals(reviewedForm.getFormDefinitionId()
				.getId(), form.getFormDefinitionId());
		assertEquals(reviewedForm.getPopulatedFormId()
				.getId(), form.getPopulatedFormId());
	}

	private ReviewedItemGroupMongoSnapshot assertReviewedItemGroup(SubmittedItemGroup reviewedItemGroup,
                                                                   ReviewContextDocument snapshot) {
		ReviewedItemGroupMongoSnapshot reviewedItemGroupMongoSnapshot = findReviewedForm(snapshot)
                .orElseThrow{new AssertionError()}
				.getReviewedItemGroups()
				.get(0);
		assertEquals(reviewedItemGroup.getId()
				.getId(), reviewedItemGroupMongoSnapshot.getId());
		assertEquals(reviewedItemGroup.isRepeating(), reviewedItemGroupMongoSnapshot.isRepeating());
		assertEquals(reviewedItemGroup.getPopulatedItemGroupId()
				.getId(), reviewedItemGroupMongoSnapshot.getPopulatedItemGroupId());
		return reviewedItemGroupMongoSnapshot;
	}

	private void assertReviewedItem(SubmittedItem reviewedItem, ReviewContextDocument snapshot) {
		ReviewedItemMongoSnapshot reviewedItemMongoSnapshot = findReviewedForm(snapshot).orElseThrow{new AssertionError()}
				.getReviewedItemGroups()
				.get(0)
				.getReviewedItems()
				.get(0);
		assertEquals(reviewedItem.getId()
				.getId(), reviewedItemMongoSnapshot.getId());
		assertEquals(reviewedItem.isKey(), reviewedItemMongoSnapshot.isKey());
		assertEquals(reviewedItem.getPopulatedItemId()
				.getId(), reviewedItemMongoSnapshot.getPopulatedItemId());
		assertLabeledValue(reviewedItem.getLabeledValue(), reviewedItemMongoSnapshot.getLabeledValue());
	}

	private void assertLabeledValue(final SubmittedLabeledValue labeledValue,
			final ReviewedLabeledValueDocument snapshot) {
		assertEquals(labeledValue.getValue(), snapshot.getValue());
		assertEquals(1, snapshot.getLabels()
				.size());
		assertLabel(labeledValue.getLabels()
				.get(0), snapshot.getLabels()
				.get(0));
	}

	private void assertLabel(final SubmittedLabel reviewedLabel, final ReviewedLabelDocument snapshot) {
		assertEquals(reviewedLabel.getText(), snapshot.getText());
		assertEquals(reviewedLabel.getLocale(), snapshot.getLocale());
	}

	private Optional<ReviewedFormMongoSnapshot> findReviewedForm(ReviewContextDocument snapshot) {
		return snapshot.getReviewedEvent()
				.getReviewedForms()
				.stream()
				.findFirst();
	}
}
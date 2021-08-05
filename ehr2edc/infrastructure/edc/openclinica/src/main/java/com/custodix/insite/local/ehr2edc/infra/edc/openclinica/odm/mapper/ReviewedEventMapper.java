package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.mapper;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.*;
import com.custodix.insite.local.ehr2edc.submitted.*;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

final class ReviewedEventMapper {

	private static final String STUDY_EVENT_REPEATKEY_FORMAT = "%s[1]/%s[1]";

	private final SubmittedEvent submittedEvent;
	private final AuditRecordMapper auditRecordMapper;

	ReviewedEventMapper(SubmittedEvent submittedEvent, ExternalEDCConnection connection) {
		this.submittedEvent = submittedEvent;
		this.auditRecordMapper = new AuditRecordMapper(submittedEvent, connection);
	}

	StudyEventData mapStudyEventData() {
		if (StringUtils.isNotBlank(submittedEvent.getEventParentId())) {
			return mapNestedEvent();
		}
		return mapEvent();
	}

	private StudyEventData mapNestedEvent() {
		String parentId = submittedEvent.getEventParentId();
		String eventId = submittedEvent.getEventDefinitionId()
				.getId();
		String repeatKey = String.format(STUDY_EVENT_REPEATKEY_FORMAT, parentId, eventId);
		return StudyEventData.newBuilder()
				.withStudyEventOID(parentId)
				.withStudyEventRepeatKey(repeatKey)
				.withStartDate(submittedEvent.getReferenceDate())
				.withFormData(mapForms())
				.build();
	}

	private StudyEventData mapEvent() {
		return StudyEventData.newBuilder()
				.withStudyEventOID(submittedEvent.getEventDefinitionId()
						.getId())
				.withStartDate(submittedEvent.getReferenceDate())
				.withFormData(mapForms())
				.build();
	}

	private List<FormData> mapForms() {
		return submittedEvent.getSubmittedForms()
				.stream()
				.map(this::mapForm)
				.collect(toList());
	}

	private FormData mapForm(SubmittedForm reviewedForm) {
		FormData.Builder builder = FormData.newBuilder()
				.withFormOid(reviewedForm.getFormDefinitionId()
						.getId())
				.withItemGroupData(mapItemGroups(reviewedForm.getSubmittedItemGroups()));
		return builder.build();
	}

	private List<ItemGroupData> mapItemGroups(List<SubmittedItemGroup> reviewedItemGroups) {
		return reviewedItemGroups.stream()
				.map(this::mapItemGroup)
				.collect(toList());
	}

	private ItemGroupData mapItemGroup(SubmittedItemGroup reviewedItemGroup) {
		ItemGroupData.Builder itemGroupBuilder = ItemGroupData.newBuilder()
				.withItemGroupOID(reviewedItemGroup.getId()
						.getId())
				.withItemData(mapItemDataList(reviewedItemGroup));

		if (reviewedItemGroup.isRepeating()) {
			addRepeatKey(itemGroupBuilder);
		}

		if (reviewedItemGroup.hasIndex()) {
			itemGroupBuilder.withItemGroupRepeatKey(reviewedItemGroup.getIndex());
		}
		return itemGroupBuilder.build();
	}

	private void addRepeatKey(ItemGroupData.Builder itemGroupDataBuilder) {
		itemGroupDataBuilder.withItemGroupRepeatKey(ItemGroupData.REPEAT_KEY);
	}

	private List<ItemData> mapItemDataList(SubmittedItemGroup reviewedItemGroup) {
		return reviewedItemGroup.getSubmittedItems()
				.stream()
				.filter(SubmittedItem::isSubmittedToEDC)
				.map(this::mapItemData)
				.collect(toList());
	}

	private ItemData mapItemData(SubmittedItem reviewedItem) {
		MeasurementUnitRef measurementUnitRef = mapMeasurementUnitRef(reviewedItem);
		return ItemData.newBuilder()
				.withItemOID(reviewedItem.getId()
						.getId())
				.withValue(reviewedItem.getLabeledValue()
						.getValue())
				.withMeasurementUnitRef(measurementUnitRef)
				.withAuditRecord(auditRecordMapper.map())
				.build();
	}

	private MeasurementUnitRef mapMeasurementUnitRef(final SubmittedItem reviewedItem) {
		return reviewedItem.getReviewedMeasurementUnitReference()
				.filter(SubmittedMeasurementUnitReference::isSubmittedToEDC)
				.map(this::mapMeasurementUnitRef)
				.orElse(null);
	}

	private MeasurementUnitRef mapMeasurementUnitRef(
			SubmittedMeasurementUnitReference reviewedMeasurementUnitReference) {
		return MeasurementUnitRef.newBuilder()
				.withMeasurementUnitOID(reviewedMeasurementUnitReference.getId())
				.build();
	}
}

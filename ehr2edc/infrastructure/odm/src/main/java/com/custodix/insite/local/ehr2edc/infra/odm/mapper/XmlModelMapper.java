package com.custodix.insite.local.ehr2edc.infra.odm.mapper;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.StudyMetaData;
import com.custodix.insite.local.ehr2edc.infra.odm.parser.model.*;
import com.custodix.insite.local.ehr2edc.metadata.model.*;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public class XmlModelMapper {

	public StudyMetaData toStudyMetaData(XmlOdm odm) {
		XmlStudy xmlStudy = odm.getStudy();
		return StudyMetaData.newBuilder()
				.withId(getStudyId(xmlStudy))
				.withName(getName(xmlStudy.getGlobalVariables()))
				.withDescription(getDescription(xmlStudy.getGlobalVariables()))
				.withMetaDataDefinition(getMetaDataDefinition(odm))
				.build();
	}

	private StudyId getStudyId(XmlStudy study) {
		return StudyId.newBuilder()
				.withId(study.getOid())
				.build();
	}

	private String getName(XmlGlobalVariables globalVariables) {
		return globalVariables.getName();
	}

	private String getDescription(XmlGlobalVariables globalVariables) {
		return globalVariables.getDescription() == null ? "" : globalVariables.getDescription();
	}

	private MetaDataDefinition getMetaDataDefinition(XmlOdm odm) {
		return MetaDataDefinition.newBuilder()
				.withId(getId(odm))
				.withEventDefinitions(getEventDefinitions(odm))
				.build();
	}

	private String getId(XmlOdm odm) {
		return odm.getStudy()
				.getMetaDataVersion()
				.getOid();
	}

	private List<EventDefinition> getEventDefinitions(final XmlOdm odm) {
		List<String> definedInProtocol = getEventsDefinedInProtocol(odm);
		return odm.getStudy()
				.getMetaDataVersion()
				.getStudyEventDefs()
				.stream()
				.filter(studyEventDef -> definedInProtocol.contains(studyEventDef.getOid()))
				.map(studyEventDef -> getEventDefinition(studyEventDef, odm))
				.collect(toList());
	}

	private List<String> getEventsDefinedInProtocol(XmlOdm odm) {
		return getProtocol(odm).map(XmlProtocol::getStudyEventRefs)
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(XmlStudyEventRef::getStudyEventOid)
				.collect(toList());
	}

	private Optional<XmlProtocol> getProtocol(XmlOdm odm) {
		return Optional.ofNullable(odm.getStudy()
				.getMetaDataVersion()
				.getProtocol());
	}

	private EventDefinition getEventDefinition(final XmlStudyEventDef studyEventDef, final XmlOdm odm) {
		return EventDefinition.newBuilder()
				.withId(getEventDefinitionId(studyEventDef))
				.withName(studyEventDef.getName())
				.withParentId(studyEventDef.getParentId())
				.withFormDefinitions(getFormDefinitionsForEvent(studyEventDef, odm))
				.build();
	}

	private EventDefinitionId getEventDefinitionId(XmlStudyEventDef studyEventDef) {
		return EventDefinitionId.of(studyEventDef.getOid());
	}

	private List<FormDefinition> getFormDefinitionsForEvent(final XmlStudyEventDef studyEventDef, final XmlOdm odm) {
		List<String> definedInStudyEvent = getFormsDefinedInEvent(studyEventDef);
		return odm.getStudy()
				.getMetaDataVersion()
				.getFormDefs()
				.stream()
				.filter(form -> definedInStudyEvent.contains(form.getOid()))
				.map(fd -> getFormDefinition(fd, odm))
				.collect(toList());
	}

	private List<String> getFormsDefinedInEvent(XmlStudyEventDef studyEventDef) {
		return studyEventDef.getFormRefs()
				.stream()
				.map(XmlFormRef::getFormOid)
				.collect(toList());
	}

	private FormDefinition getFormDefinition(final XmlFormDef fd, final XmlOdm odm) {
		List<ItemGroupDefinition> itemGroups = fd.getItemGroupRefs()
				.stream()
				.map(ig -> toItemGroup(ig, odm))
				.collect(toList());
		return FormDefinition.newBuilder()
				.withId(getFormDefintionId(fd))
				.withName(getName(fd).orElse(getFormDefintionId(fd).getId()))
				.withItemGroupDefinitions(itemGroups)
				.withLocalLab(getLocalLab(fd))
				.build();
	}

	private FormDefinitionId getFormDefintionId(XmlFormDef formDef) {
		return FormDefinitionId.newBuilder()
				.withId(formDef.getOid())
				.build();
	}

	private Optional<String> getName(XmlFormDef formDef) {
		String name = formDef.getName();
		if (StringUtils.isEmpty(name)) {
			return Optional.empty();
		} else {
			return Optional.of(name);
		}
	}

	private LabName getLocalLab(XmlFormDef formDef) {
		return LabName.of(formDef.getLocalLab());
	}

	private ItemGroupDefinition toItemGroup(final XmlItemGroupRef ig, final XmlOdm odm) {
		XmlItemGroupDef itemGroupDef = odm.getStudy()
				.getMetaDataVersion()
				.getItemGroupDef(ig.getItemGroupOid())
				.orElseThrow(() -> new IllegalStateException(
						"Form contains reference to itemgroup that is not defined: " + ig.getItemGroupOid()));
		List<ItemDefinition> items = Optional.ofNullable(itemGroupDef.getItemRefs())
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(i -> toItem(i, odm))
				.collect(toList());
		return ItemGroupDefinition.newBuilder()
				.withId(ItemGroupDefinitionId.of(itemGroupDef.getOid()))
				.withName(itemGroupDef.getName())
				.withItemDefinitions(items)
				.withRepeating(itemGroupDef.isRepeating())
				.build();
	}

	private ItemDefinition toItem(final XmlItemRef i, final XmlOdm odm) {
		XmlItemDef itemDef = odm.getStudy()
				.getMetaDataVersion()
				.getItemDef(i.getItemOid())
				.orElseThrow(() -> new IllegalStateException(
						"Item group contains reference to item that is not defined: " + i.getItemOid()));

		ItemDefinition.Builder builder = ItemDefinition.newBuilder()
				.withId(ItemDefinitionId.of(itemDef.getOid()))
				.withLabel(ItemLabel.newBuilder().withName(itemDef.getName()).build())
				.withDataType(itemDef.getDataType())
				.withLength(itemDef.getLength() == 0 ? null : itemDef.getLength())
				.withMeasurementUnits(getMeasurementUnits(odm, itemDef));

		if (itemDef.getCodeListRef() != null) {
			builder.withCodeList(getCodeList(itemDef.getCodeListRef(), odm));
		}

		withOptionalQuestion(itemDef, builder);
		return builder.build();

	}

	private List<MeasurementUnit> getMeasurementUnits(XmlOdm odm, XmlItemDef itemDef) {
		List<MeasurementUnit> measurementUnits = Collections.emptyList();
		if (itemDef.getMeasurementUnitRefs() != null) {
			measurementUnits = itemDef.getMeasurementUnitRefs()
					.stream()
					.map(mu -> getMeasurementUnit(mu, odm))
					.collect(toList());
		}
		return measurementUnits;
	}

	private CodeList getCodeList(final XmlCodeListRef codeListRef, final XmlOdm odm) {
		XmlCodeList codeList = odm.getStudy()
				.getMetaDataVersion()
				.getCodeList(codeListRef.getCodeListOid())
				.orElseThrow(() -> new IllegalStateException(
						"Item contains reference to code list that is not defined: " + codeListRef.getCodeListOid()));
		return CodeList.newBuilder()
				.withId(codeList.getOid())
				.build();
	}

	private MeasurementUnit getMeasurementUnit(final XmlMeasurementUnitRef mu, final XmlOdm odm) {
		XmlMeasurementUnit xmlMeasurementUnit = odm.getStudy()
				.getBasicDefinitions()
				.getMeasurementUnit(mu.getMeasurementUnitOid())
				.orElseThrow(() -> new IllegalStateException(
						"Item contains reference to measurement unit that is not defined: "
								+ mu.getMeasurementUnitOid()));
		return MeasurementUnit.newBuilder()
				.withId(xmlMeasurementUnit.getOid())
				.withName(xmlMeasurementUnit.getName())
				.build();
	}

	private void withOptionalQuestion(XmlItemDef itemDef, ItemDefinition.Builder builder) {
		if (hasQuestion(itemDef)) {
			builder.withLabel(ItemLabel.newBuilder().withName(itemDef.getName()).withQuestion(getQuestion(itemDef)).build());
		}
	}

	private boolean hasQuestion(XmlItemDef itemDef) {
		return itemDef.getQuestion() != null;
	}

	private Question getQuestion(XmlItemDef itemDef) {
		ensureQuestionHasTranslatedText(itemDef);
		return new Question(translatedTextByLocale(itemDef));
	}

	private Map<Locale, String> translatedTextByLocale(XmlItemDef itemDef) {
		Map<Locale, String> translatedTextByLocale = new HashMap<>();
		itemDef.getQuestion().getTranslatedTexts().forEach(
				xmlTranslatedText -> translatedTextByLocale.put(
						Locale.forLanguageTag(xmlTranslatedText.getLang()),
						xmlTranslatedText.getText()));
		return translatedTextByLocale;
	}

	private void ensureQuestionHasTranslatedText(XmlItemDef itemDef) {
		if (hasNoTranslatedText(itemDef)) {
			throw new IllegalStateException(
					String.format("Item %s contains question with no translated text", itemDef.getOid()));
		}
	}

	private boolean hasNoTranslatedText(XmlItemDef itemDef) {
		return itemDef.getQuestion().getTranslatedTexts() == null
				|| itemDef.getQuestion().getTranslatedTexts().isEmpty();
	}
}

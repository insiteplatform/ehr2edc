package com.custodix.insite.local.ehr2edc.jackson;


import com.custodix.insite.local.ehr2edc.jackson.deserializers.*;
import com.custodix.insite.local.ehr2edc.jackson.serializers.*;
import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class VocabularyModule {
	private VocabularyModule() {
	}

	public static SimpleModule create() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(SubjectId.class, new SubjectIdSerializer());
		module.addSerializer(EDCSubjectReference.class, new EDCSubjectReferenceSerializer());
		module.addSerializer(StudyId.class, new StudyIdSerializer());
		module.addSerializer(PatientExporterId.class, new PatientExporterIdSerializer());
		module.addSerializer(SubmittedEventId.class, new SubmittedEventIdSerializer());
		module.addSerializer(UserIdentifier.class, new UserIdentifierSerializer());
		module.addSerializer(ExternalSiteId.class, new ExternalSiteIdSerializer());

		module.addDeserializer(PatientCDWReference.class, new PatientIdDeserializer());
		module.addDeserializer(UserIdentifier.class, new UserIdentifierDeserializer());
		module.addDeserializer(StudyId.class, new StudyIdDeserializer());
		module.addDeserializer(SubjectId.class, new SubjectIdDeserializer());
		module.addDeserializer(EDCSubjectReference.class, new EDCSubjectReferenceDeserializer());
		module.addDeserializer(ExternalSiteId.class, new ExternalSiteIdDeserializer());
		addFormSerializers(module);
		addEventSerializers(module);
		addItemSerializers(module);
		addItemGroupSerializers(module);
		return module;
	}

	private static void addEventSerializers(SimpleModule module) {
		module.addSerializer(EventDefinitionId.class, new EventDefinitionIdSerializer());
		module.addDeserializer(EventDefinitionId.class, new EventDefinitionIdDeserializer());
		module.addSerializer(EventId.class, new EventIdSerializer());
		module.addDeserializer(EventId.class, new EventIdDeserializer());
	}

	private static void addFormSerializers(SimpleModule module) {
		module.addDeserializer(FormDefinitionId.class, new FormDefinitionIdDeserializer());
		module.addDeserializer(FormId.class, new FormIdDeserializer());

		module.addSerializer(FormDefinitionId.class, new FormDefinitionIdSerializer());
		module.addSerializer(FormId.class, new FormIdSerializer());
	}

	private static void addItemSerializers(SimpleModule module) {
		module.addSerializer(ItemId.class, new ItemIdSerializer());
		module.addSerializer(ItemDefinitionId.class, new ItemDefinitionIdSerializer());
		module.addKeySerializer(ItemDefinitionId.class, new ItemDefinitionIdKeySerializer());
		module.addSerializer(SubmittedItemId.class, new SubmissionItemIdSerializer());

		module.addDeserializer(ItemId.class, new ItemIdDeserializer());
		module.addDeserializer(ItemDefinitionId.class, new ItemDefinitionIdDeserializer());
		module.addKeyDeserializer(ItemDefinitionId.class, new ItemDefinitionIdKeyDeserializer());
		module.addDeserializer(SubmittedItemId.class, new SubmittedItemIdIdDeserializer());
	}

	private static void addItemGroupSerializers(SimpleModule module) {
		module.addSerializer(ItemGroupId.class, new ItemGroupIdSerializer());
		module.addSerializer(ItemGroupDefinitionId.class, new ItemGroupDefinitionIdSerializer());

		module.addDeserializer(ItemGroupId.class, new ItemGroupIdDeserializer());
		module.addDeserializer(ItemGroupDefinitionId.class, new ItemGroupDefinitionIdDeserializer());
	}
}
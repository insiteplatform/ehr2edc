package com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.snapshots.*;
import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class AppMongoJacksonMixinsModule {
	private AppMongoJacksonMixinsModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addDomainMixins(simpleModule);
		addVocabularyMixins(simpleModule);

		return simpleModule;
	}

	private static void addDomainMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(StudySnapshot.class, StudySnapshotMixin.class);
		simpleModule.setMixInAnnotation(CodeListSnapshot.class, CodeListSnapshotMixin.class);
		simpleModule.setMixInAnnotation(EventDefinitionSnapshot.class, EventDefinitionSnapshotMixin.class);
		simpleModule.setMixInAnnotation(FormDefinitionSnapshot.class, FormDefinitionSnapshotMixin.class);
		simpleModule.setMixInAnnotation(InvestigatorSnapshot.class, InvestigatorSnapshotMixin.class);
		simpleModule.setMixInAnnotation(ItemGroupDefinitionSnapshot.class, ItemGroupDefinitionSnapshotMixin.class);
		simpleModule.setMixInAnnotation(ItemDefinitionSnapshot.class, ItemDefinitionSnapshotMixin.class);
		simpleModule.setMixInAnnotation(MeasurementUnitSnapshot.class, MeasurementUnitSnapshotMixin.class);
		simpleModule.setMixInAnnotation(MetaDataDefinitionSnapshot.class, MetaDataDefinitionSnapshotMixin.class);
		simpleModule.setMixInAnnotation(StudySnapshot.class, StudySnapshotMixin.class);
		simpleModule.setMixInAnnotation(SubjectSnapshot.class, SubjectSnapshotMixin.class);

		simpleModule.setMixInAnnotation(ItemQueryMapping.class, ItemQueryMappingMixin.class);
	}

	private static void addVocabularyMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(StudyId.class, StudyIdMixin.class);
		simpleModule.setMixInAnnotation(ExternalEDCConnection.class, ExternalEDCConnectionMixin.class);
		simpleModule.setMixInAnnotation(ExternalSiteId.class, ExternalSiteIdMixin.class);
		simpleModule.setMixInAnnotation(PatientCDWReference.class, PatientIdMixin.class);
		simpleModule.setMixInAnnotation(SubjectId.class, SubjectIdMixin.class);
		simpleModule.setMixInAnnotation(EventDefinitionId.class, EventDefinitionIdMixin.class);
		simpleModule.setMixInAnnotation(FormDefinitionId.class, FormDefintionIdMixin.class);
		simpleModule.setMixInAnnotation(UserIdentifier.class, UserIdentifierMixin.class);
	}
}

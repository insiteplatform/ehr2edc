package com.custodix.insite.local.ehr2edc.query.populator.jackson;

import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class QueryJacksonMixinsModule {
	private QueryJacksonMixinsModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addDomainMixins(simpleModule);
		addVocabularyMixins(simpleModule);

		return simpleModule;
	}

	private static void addDomainMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(ItemQueryMapping.class, ItemQueryMappingMixin.class);
		simpleModule.setMixInAnnotation(SubjectIdMixin.class, SubjectIdMixin.class);
	}

	private static void addVocabularyMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(SubjectId.class, SubjectIdMixin.class);
	}

}

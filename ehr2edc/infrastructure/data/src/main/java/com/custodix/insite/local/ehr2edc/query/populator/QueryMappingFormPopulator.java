package com.custodix.insite.local.ehr2edc.query.populator;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService;
import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

class QueryMappingFormPopulator {
	private final FormPopulatorLogger formPopulatorLogger;
	private final ItemQueryMappingService itemQueryMappingService;
	private final EhrGatewayFactoryFactory ehrGatewayFactory;

	QueryMappingFormPopulator(FormPopulatorLogger formPopulatorLogger, ItemQueryMappingService itemQueryMappingService,
			EhrGatewayFactoryFactory ehrGatewayFactory) {
		this.formPopulatorLogger = formPopulatorLogger;
		this.itemQueryMappingService = itemQueryMappingService;
		this.ehrGatewayFactory = ehrGatewayFactory;
	}

	public PopulatedForm populateForm(PopulationSpecification specification, FormDefinition formDefinition) {

		formPopulatorLogger.startLogging(formDefinition, specification);
		try {
			return buildForm(formDefinition, specification,
					itemQueryMappingService.toItemQueryMappingMap(specification.getStudyItemQueryMappings()));
		} finally {
			formPopulatorLogger.stopLogging();
		}
	}

	private PopulatedForm buildForm(FormDefinition formDefinition, PopulationSpecification specification,
			Map<ItemDefinitionId, ItemQueryMapping> itemDefinitionIdItemQueryMappingMap) {
		final List<PopulatedItemGroup> itemGroups = getItemGroups(formDefinition, specification,
				itemDefinitionIdItemQueryMappingMap);
		return PopulatedForm.newBuilder()
				.withInstanceId(FormId.of(randomUUID().toString()))
				.withName(formDefinition.getName())
				.withFormDefinitionId(formDefinition.getId())
				.withItemGroups(itemGroups)
				.withLocalLab(formDefinition.getLocalLab())
				.build();
	}

	private List<PopulatedItemGroup> getItemGroups(FormDefinition formDefinition, PopulationSpecification specification,
			Map<ItemDefinitionId, ItemQueryMapping> itemDefinitionIdItemQueryMappingMap) {
		QueryFactory queryFactory = new QueryFactory(itemDefinitionIdItemQueryMappingMap, specification.getSubjectId(),
				specification.getPatientCDWReference());
		return new QueryResultGenerator(specification, formPopulatorLogger, ehrGatewayFactory).calculateResults(
				formDefinition, queryFactory);
	}
}

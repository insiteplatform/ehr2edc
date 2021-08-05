package com.custodix.insite.local.ehr2edc.domain.service;

import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson;
import com.custodix.insite.local.ehr2edc.ItemQueryMappings;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public interface ItemQueryMappingService {

	ItemQueryMappingJson toItemQueryMappingJson(final Query query, final List<Projector> projectors);

	Map<ItemDefinitionId, ItemQueryMapping> toItemQueryMappingMap(final ItemQueryMappings itemQueryMappings);
}

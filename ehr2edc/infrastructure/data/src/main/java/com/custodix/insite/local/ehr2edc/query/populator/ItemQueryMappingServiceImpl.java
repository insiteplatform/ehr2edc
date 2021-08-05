package com.custodix.insite.local.ehr2edc.query.populator;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson;
import com.custodix.insite.local.ehr2edc.ItemQueryMappings;
import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemQueryMappingServiceImpl implements ItemQueryMappingService {

	private final ObjectMapper objectMapper;

	public ItemQueryMappingServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public ItemQueryMappingJson toItemQueryMappingJson(final Query query, final List<Projector> projectors) {
		try {
			ItemQueryMapping itemQueryMapping = ItemQueryMapping.newBuilder()
					.withProjectors(projectors)
					.withQuery(query)
					.build();
			return ItemQueryMappingJson.create(objectMapper.writeValueAsString(itemQueryMapping));
		} catch (JsonProcessingException e) {
			throw new SystemException("Cannot serialize query and projectors. ", e);
		}
	}

	@Override
	public Map<ItemDefinitionId, ItemQueryMapping> toItemQueryMappingMap(final ItemQueryMappings itemQueryMappings) {
		return itemQueryMappings.getItemQueryMappingJsonMap().entrySet()
				.stream()
				.collect(toMap(
					Map.Entry::getKey,
					e -> toItemQueryMapping(e.getValue()))
				);
	}

	private ItemQueryMapping toItemQueryMapping(final ItemQueryMappingJson itemQueryMappingJson) {
		try {
			return objectMapper.readValue(itemQueryMappingJson.getValue(), ItemQueryMapping.class );
		} catch (IOException ex) {
			throw new SystemException(String.format("Cannot convert json string '%s' to an ItemQueryMapping", itemQueryMappingJson.getValue()), ex);
		}
	}
}

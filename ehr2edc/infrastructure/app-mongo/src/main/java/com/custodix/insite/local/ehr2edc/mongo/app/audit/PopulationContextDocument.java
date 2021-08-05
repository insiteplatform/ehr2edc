package com.custodix.insite.local.ehr2edc.mongo.app.audit;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.audit.PopulationContext;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.PopulationContextId;

@Document(collection = PopulationContextDocument.COLLECTION)
public class PopulationContextDocument {
	static final String COLLECTION = "PopulationContext";

	private final String id;
	private final String eventId;
	private final org.bson.Document definition;
	private final org.bson.Document itemQueryMappings;
	private final List<org.bson.Document> datapoints;

	@PersistenceConstructor
	PopulationContextDocument(String id, String eventId, org.bson.Document definition,
			org.bson.Document itemQueryMappings, List<org.bson.Document> datapoints) {
		this.id = id;
		this.eventId = eventId;
		this.definition = definition;
		this.itemQueryMappings = itemQueryMappings;
		this.datapoints = datapoints;
	}

	private PopulationContextDocument(Builder builder) {
		id = builder.id;
		eventId = builder.eventId;
		definition = builder.definition;
		itemQueryMappings = builder.itemQueryMappings;
		datapoints = builder.datapoints;
	}

	public String getId() {
		return id;
	}

	String getEventId() {
		return eventId;
	}

	public static PopulationContextDocument from(PopulationContext populationContext) {
		return newBuilder().withId(populationContext.getId()
				.getId())
				.withEventId(populationContext.getEventId()
						.getId())
				.withItemQueryMappings(parse(populationContext.getItemQueryMappingsJson()))
				.withDefinition(parse(populationContext.getEventDefinitionJson()))
				.withDatapoints(parseDatapoints(populationContext))
				.build();
	}

	private static List<org.bson.Document> parseDatapoints(PopulationContext populationContext) {
		return populationContext.getDatapointsJsons().stream()
		.map(PopulationContextDocument::parse)
		.collect(toList());
	}

	private static org.bson.Document parse(String json) {
		return org.bson.Document.parse(json.replaceAll("\\.", "(dot)"));
	}

	public PopulationContext restore() {
		return PopulationContext.newBuilder()
				.withId(PopulationContextId.of(id))
				.withEventId(EventId.of(eventId))
				.withItemQueryMappingsJson(toJson(itemQueryMappings))
				.withEventDefinitionJson(toJson(definition))
				.withDatapointsJsons(toJson(datapoints))
				.build();
	}

	private List<String> toJson(List<org.bson.Document> documents) {
		return documents.stream()
				.map(this::toJson)
				.collect(toList());
	}

	private String toJson(org.bson.Document document) {
		return document.toJson().replaceAll("\\(dot\\)", ".");
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private String eventId;
		private org.bson.Document definition;
		private org.bson.Document itemQueryMappings;
		private List<org.bson.Document> datapoints;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withEventId(String eventId) {
			this.eventId = eventId;
			return this;
		}

		public Builder withDefinition(org.bson.Document definitionDocument) {
			this.definition = definitionDocument;
			return this;
		}

		public Builder withItemQueryMappings(org.bson.Document mappingsDocument) {
			this.itemQueryMappings = mappingsDocument;
			return this;
		}

		public Builder withDatapoints(List<org.bson.Document> datapoints) {
			this.datapoints = datapoints;
			return this;
		}

		public PopulationContextDocument build() {
			return new PopulationContextDocument(this);
		}
	}
}

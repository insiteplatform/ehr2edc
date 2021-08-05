package com.custodix.insite.local.ehr2edc.audit;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.PopulationContextId;

public final class PopulationContext {

	private final PopulationContextId id;
	private final EventId eventId;
	private final String eventDefinitionJson;
	private final String itemQueryMappingsJson;
	private final List<String> datapointsJsons;

	private PopulationContext(Builder builder) {
		id = builder.id;
		eventId = builder.eventId;
		eventDefinitionJson = builder.eventDefinitionJson;
		itemQueryMappingsJson = builder.itemQueryMappingsJson;
		datapointsJsons = builder.datapointsJsons;
	}

	public PopulationContextId getId() {
		return id;
	}

	public EventId getEventId() {
		return eventId;
	}

	public String getEventDefinitionJson() {
		return eventDefinitionJson;
	}

	public String getItemQueryMappingsJson() {
		return itemQueryMappingsJson;
	}

	public List<String> getDatapointsJsons() {
		return datapointsJsons;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PopulationContextId id;
		private EventId eventId;
		private String eventDefinitionJson;
		private String itemQueryMappingsJson;
		private List<String> datapointsJsons;

		private Builder() {
		}

		public Builder withId(PopulationContextId id) {
			this.id = id;
			return this;
		}

		public Builder withEventId(EventId eventId) {
			this.eventId = eventId;
			return this;
		}

		public Builder withEventDefinitionJson(String eventDefinitionJson) {
			this.eventDefinitionJson = eventDefinitionJson;
			return this;
		}

		public Builder withItemQueryMappingsJson(String itemQueryMappingsJson) {
			this.itemQueryMappingsJson = itemQueryMappingsJson;
			return this;
		}

		public Builder withDatapointsJsons(List<String> datapointsJsons) {
			this.datapointsJsons = datapointsJsons;
			return this;
		}

		public PopulationContext build() {
			return new PopulationContext(this);
		}
	}
}

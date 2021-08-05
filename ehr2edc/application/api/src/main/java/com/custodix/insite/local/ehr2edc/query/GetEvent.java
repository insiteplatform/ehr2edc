package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public interface GetEvent {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response getEvent(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final EventId eventId;

		@NotNull
		@Valid
		@AuthorizationCorrelator
		private final SubjectId subjectId;

		private Request(Builder builder) {
			eventId = builder.eventId;
			subjectId = builder.subjectId;
		}

		public EventId getEventId() {
			return eventId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private EventId eventId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withEventId(EventId val) {
				eventId = val;
				return this;
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final Event event;

		private Response(Builder builder) {
			event = builder.event;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Event getEvent() {
			return event;
		}

		public static final class Builder {
			private Event event;

			private Builder() {
			}

			public Builder withEvent(Event event) {
				this.event = event;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Event {
		private final List<Form> forms;
		private final Instant lastSubmissionTime;
		private final String name;

		private Event(final Builder builder) {
			forms = builder.forms;
			lastSubmissionTime = builder.lastSubmissionTime;
			name = builder.name;
		}

		public List<Form> getForms() {
			return forms;
		}

		public Instant getLastSubmissionTime() {
			return lastSubmissionTime;
		}

		public String getName() {
			return name;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<Form> forms;
			private Instant lastSubmissionTime;
			private String name;

			private Builder() {
			}

			public Builder withForms(List<Form> val) {
				forms = val;
				return this;
			}

			public Builder withLastSubmissionTime(Instant val) {
				lastSubmissionTime = val;
				return this;
			}

			public Builder withName(final String val) {
				name = val;
				return this;
			}

			public Event build() {
				return new Event(this);
			}
		}
	}

	final class Form {
		private final FormId id;
		private final String name;
		private final Instant populationTime;
		private final LocalDate referenceDate;
		private final List<ItemGroup> itemGroups;

		private Form(Builder builder) {
			id = builder.id;
			name = builder.name;
			populationTime = builder.populationTime;
			referenceDate = builder.referenceDate;
			itemGroups = builder.itemGroups;
		}

		public FormId getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Instant getPopulationTime() {
			return populationTime;
		}

		public LocalDate getReferenceDate() {
			return referenceDate;
		}

		public List<ItemGroup> getItemGroups() {
			return itemGroups;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private FormId id;
			private String name;
			private Instant populationTime;
			private LocalDate referenceDate;
			private List<ItemGroup> itemGroups;

			private Builder() {
			}

			public Builder withId(FormId val) {
				id = val;
				return this;
			}

			public Builder withName(String val) {
				name = val;
				return this;
			}

			public Builder withPopulationTime(Instant val) {
				populationTime = val;
				return this;
			}

			public Builder withReferenceDate(LocalDate val) {
				referenceDate = val;
				return this;
			}

			public Builder withItemGroups(List<ItemGroup> val) {
				itemGroups = val;
				return this;
			}

			public Form build() {
				return new Form(this);
			}
		}
	}

	final class ItemGroup {
		private final ItemGroupId id;
		private final String name;
		private final List<Item> items;

		private ItemGroup(Builder builder) {
			id = builder.id;
			name = builder.name;
			items = builder.items;
		}

		public ItemGroupId getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public List<Item> getItems() {
			return items;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private ItemGroupId id;
			private String name;
			private List<Item> items;

			private Builder() {
			}

			public Builder withId(ItemGroupId val) {
				id = val;
				return this;
			}

			public Builder withName(String val) {
				name = val;
				return this;
			}

			public Builder withItems(List<Item> val) {
				items = val;
				return this;
			}

			public ItemGroup build() {
				return new ItemGroup(this);
			}
		}
	}

	final class Item {
		private final ItemId id;
		private final String name;
		private final String value;
		private final String valueLabel;
		private final String unit;
		private final boolean exportable;

		private Item(Builder builder) {
			id = builder.id;
			name = builder.name;
			value = builder.value;
			valueLabel = builder.valueLabel;
			unit = builder.unit;
			exportable = builder.exportable;
		}

		public ItemId getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getValueLabel() {
			return valueLabel;
		}

		public String getUnit() {
			return unit;
		}

		public boolean isExportable() {
			return exportable;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private ItemId id;
			private String name;
			private String value;
			private String valueLabel;
			private String unit;
			private boolean exportable;

			private Builder() {
			}

			public Builder withId(ItemId val) {
				id = val;
				return this;
			}

			public Builder withName(String val) {
				name = val;
				return this;
			}

			public Builder withValue(String val) {
				value = val;
				return this;
			}

			public Builder withValueLabel(String val) {
				valueLabel = val;
				return this;
			}

			public Builder withUnit(String val) {
				unit = val;
				return this;
			}

			public Builder withExportable(boolean val) {
				exportable = val;
				return this;
			}

			public Item build() {
				return new Item(this);
			}
		}
	}
}
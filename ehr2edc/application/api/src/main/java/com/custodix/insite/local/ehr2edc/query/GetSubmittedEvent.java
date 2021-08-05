package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public interface GetSubmittedEvent {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response get(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final SubmittedEventId submittedEventId;

		private Request(Builder builder) {
			submittedEventId = builder.submittedEventId;
		}

		public SubmittedEventId getSubmittedEventId() {
			return submittedEventId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubmittedEventId submittedEventId;

			private Builder() {
			}

			public Builder withSubmittedEventId(SubmittedEventId val) {
				submittedEventId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final Instant reviewTime;
		private final List<Form> reviewedForms;
		private final UserIdentifier reviewer;
		private final Instant populationTime;
		private final LocalDate referenceDate;
		private final String populator;

		private Response(Builder builder) {
			reviewTime = builder.reviewTime;
			reviewedForms = builder.reviewedForms;
			reviewer = builder.reviewer;
			populationTime = builder.populationTime;
			referenceDate = builder.referenceDate;
			populator = builder.populator;
		}

		public Instant getReviewTime() {
			return reviewTime;
		}

		public List<Form> getReviewedForms() {
			return reviewedForms;
		}

		public UserIdentifier getReviewer() {
			return reviewer;
		}

		public Instant getPopulationTime() {
			return populationTime;
		}

		public LocalDate getReferenceDate() {
			return referenceDate;
		}

		public String getPopulator() {
			return populator;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private Instant reviewTime;
			private List<Form> reviewedForms;
			private UserIdentifier reviewer;
			private Instant populationTime;
			private LocalDate referenceDate;
			private String populator;

			private Builder() {
			}

			public Builder withReviewTime(Instant val) {
				reviewTime = val;
				return this;
			}

			public Builder withReviewedForms(List<Form> val) {
				reviewedForms = val;
				return this;
			}

			public Builder withReviewer(UserIdentifier val) {
				reviewer = val;
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

			public Builder withPopulator(String val) {
				populator = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Form {
		private final FormId id;
		private final String name;
		private final List<ItemGroup> itemGroups;

		private Form(Builder builder) {
			id = builder.id;
			name = builder.name;
			itemGroups = builder.itemGroups;
		}

		public FormId getId() {
			return id;
		}

		public String getName() {
			return name;
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
		private final ItemGroupDefinitionId id;
		private final String name;
		private final List<Item> items;

		private ItemGroup(Builder builder) {
			id = builder.id;
			name = builder.name;
			items = builder.items;
		}

		public ItemGroupDefinitionId getId() {
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
			private ItemGroupDefinitionId id;
			private String name;
			private List<Item> items;

			private Builder() {
			}

			public Builder withId(ItemGroupDefinitionId val) {
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
		private final SubmittedItemId id;
		private final String name;
		private final String value;
		private final String valueLabel;
		private final String unit;
		private final boolean submittedToEdc;

		private Item(Builder builder) {
			id = builder.id;
			name = builder.name;
			value = builder.value;
			valueLabel = builder.valueLabel;
			unit = builder.unit;
			submittedToEdc = builder.submittedToEdc;
		}

		public SubmittedItemId getId() {
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

		public boolean isSubmittedToEdc() {
			return submittedToEdc;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubmittedItemId id;
			private String name;
			private String value;
			private String valueLabel;
			private String unit;
			private boolean submittedToEdc;

			private Builder() {
			}

			public Builder withId(SubmittedItemId val) {
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

			public Builder withSubmittedToEdc(boolean val) {
				submittedToEdc = val;
				return this;
			}

			public Item build() {
				return new Item(this);
			}
		}
	}
}
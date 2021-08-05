package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public interface SubmitReviewedEvent {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response submit(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@NotNull
		@Valid
		@AuthorizationCorrelator
		private final EventId eventId;

		@NotEmpty
		private final List<@Valid @NotNull ReviewedForm> reviewedForms;

		private Request(Builder builder) {
			eventId = builder.eventId;
			reviewedForms = builder.reviewedForms;
		}

		public List<ReviewedForm> getReviewedForms() {
			return reviewedForms;
		}

		public EventId getEventId() {
			return eventId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private List<ReviewedForm> reviewedForms;
			private EventId eventId;

			private Builder() {
			}

			public Request build() {
				return new Request(this);
			}

			public Builder withEventId(EventId val) {
				eventId = val;
				return this;
			}

			public Builder withReviewedForms(List<ReviewedForm> val) {
				reviewedForms = val;
				return this;
			}
		}
	}

	@JsonDeserialize(builder = ReviewedForm.Builder.class)
	final class ReviewedForm {
		@NotNull
		@Valid
		private final FormId id;
		@NotEmpty
		private final List<@Valid @NotNull ItemGroup> itemGroups;
		@Valid
		private final LabName labName;

		private ReviewedForm(Builder builder) {
			id = builder.id;
			itemGroups = builder.itemGroups;
			labName = builder.labName;
		}

		public FormId getId() {
			return id;
		}

		public List<ItemGroup> getItemGroups() {
			return itemGroups;
		}

		public LabName getLabName() { return labName; }

		public static Builder newBuilder() {
			return new Builder();
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private FormId id;
			private List<ItemGroup> itemGroups;
			private LabName labName;

			private Builder() {
			}

			public Builder withId(FormId val) {
				id = val;
				return this;
			}

			public Builder withItemGroups(List<ItemGroup> val) {
				itemGroups = val;
				return this;
			}

			public Builder withLabName(LabName val) {
				labName = val;
				return this;
			}

			public ReviewedForm build() {
				return new ReviewedForm(this);
			}
		}
	}

	@JsonDeserialize(builder = ItemGroup.Builder.class)
	final class ItemGroup {
		@NotNull
		@Valid
		private final ItemGroupId id;
		@NotEmpty
		private final List<@Valid @NotNull Item> items;

		private ItemGroup(Builder builder) {
			id = builder.id;
			items = builder.items;
		}

		public ItemGroupId getId() {
			return id;
		}

		public List<Item> getItems() {
			return items;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private ItemGroupId id;
			private List<Item> items;

			private Builder() {
			}

			public Builder withId(ItemGroupId val) {
				id = val;
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

	@JsonDeserialize(builder = Item.Builder.class)
	final class Item {
		@NotNull
		@Valid
		private final ItemId id;

		private Item(Builder builder) {
			id = builder.id;
		}

		public ItemId getId() {
			return id;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private ItemId id;

			private Builder() {
			}

			public Builder withId(ItemId val) {
				id = val;
				return this;
			}

			public Item build() {
				return new Item(this);
			}
		}
	}

	final class Response {
		private final SubmittedEventId submittedEventId;

		private Response(Builder builder) {
			submittedEventId = builder.submittedEventId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public SubmittedEventId getSubmittedEventId() {
			return submittedEventId;
		}

		public static final class Builder {
			private SubmittedEventId submittedEventId;

			private Builder() {
			}

			public Builder withSubmittedEventId(SubmittedEventId submittedEventId) {
				this.submittedEventId = submittedEventId;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
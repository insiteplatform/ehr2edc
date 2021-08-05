package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface GetItemProvenance {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response get(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final SubjectId subjectId;
		@NotNull
		@Valid
		private final EventId eventId;
		@NotNull
		@Valid
		private final ItemId itemId;

		private Request(Builder builder) {
			subjectId = builder.subjectId;
			eventId = builder.eventId;
			itemId = builder.itemId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EventId getEventId() {
			return eventId;
		}

		public ItemId getItemId() {
			return itemId;
		}

		public static final class Builder {
			private SubjectId subjectId;
			private EventId eventId;
			private ItemId itemId;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Builder withEventId(EventId eventId) {
				this.eventId = eventId;
				return this;
			}

			public Builder withItemId(ItemId itemId) {
				this.itemId = itemId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<ProvenanceGroup> groups;
		private final List<ProvenanceItem> items;

		private Response(Builder builder) {
			groups = builder.groups;
			items = builder.items;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public List<ProvenanceGroup> getGroups() {
			return groups;
		}

		public List<ProvenanceItem> getItems() {
			return items;
		}

		public static final class Builder {
			private List<ProvenanceGroup> groups = new ArrayList<>();
			private List<ProvenanceItem> items = new ArrayList<>();

			private Builder() {
			}

			public Builder withGroups(List<ProvenanceGroup> groups) {
				this.groups = groups;
				return this;
			}

			public Builder withItems(List<ProvenanceItem> items) {
				this.items = items;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class ProvenanceGroup {
		private final String label;
		private final List<ProvenanceItem> items;

		public ProvenanceGroup(String label, List<ProvenanceItem> items) {
			this.label = label;
			this.items = items;
		}

		public String getLabel() {
			return label;
		}

		public List<ProvenanceItem> getItems() {
			return items;
		}
	}

	final class ProvenanceItem {
		private final String label;
		private final String value;

		public ProvenanceItem(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}
	}
}
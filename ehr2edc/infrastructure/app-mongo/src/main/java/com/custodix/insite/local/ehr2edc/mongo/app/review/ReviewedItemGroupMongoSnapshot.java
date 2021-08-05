package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ReviewedItemGroupMongoSnapshot {
	private final String id;
	private final String name;
	private final List<ReviewedItemMongoSnapshot> reviewedItems;
	private final boolean repeating;
	private final String index;
	private final String populatedItemGroupId;

	@PersistenceConstructor
	ReviewedItemGroupMongoSnapshot(String id, final String name, List<ReviewedItemMongoSnapshot> reviewedItems,
			Boolean repeating, String index, String populatedItemGroupId) {
		this.id = id;
		this.name = name;
		this.reviewedItems = reviewedItems;
		this.repeating = repeating;
		this.index = index;
		this.populatedItemGroupId = populatedItemGroupId;
	}

	private ReviewedItemGroupMongoSnapshot(Builder builder) {
		id = builder.id;
		name = builder.name;
		reviewedItems = builder.reviewedItems;
		repeating = builder.repeating;
		index = builder.index;
		populatedItemGroupId = builder.populatedItemGroupId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public List<ReviewedItemMongoSnapshot> getReviewedItems() {
		return reviewedItems;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public String getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getPopulatedItemGroupId() {
		return populatedItemGroupId;
	}

	public static ReviewedItemGroupMongoSnapshot of(SubmittedItemGroup reviewedItemGroup,
                                                    ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return newBuilder().withId(reviewedItemGroup.getId().getId())
				.withName(reviewedItemGroup.getName())
				.withRepeating(reviewedItemGroup.isRepeating())
				.withReviewedItems(toReviewedItems(reviewedItemGroup, reviewedProvenanceDataPointDocumentFactory))
				.withIndex(reviewedItemGroup.getIndex())
				.withPopulatedItemGroupId(reviewedItemGroup.getPopulatedItemGroupId().getId())
				.build();
	}

	SubmittedItemGroup toReviewedItemGroup() {
		return SubmittedItemGroup.newBuilder()
				.withId(ItemGroupDefinitionId.of(id))
				.withName(name)
				.withSubmittedItems(toReviewedItems())
				.withRepeating(repeating)
				.withIndex(index)
				.withPopulatedItemGroupId(ItemGroupId.of(populatedItemGroupId))
				.build();
	}

	private List<SubmittedItem> toReviewedItems() {
		return reviewedItems.stream()
				.map(ReviewedItemMongoSnapshot::toReviewedItem)
				.collect(toList());
	}

	private static List<ReviewedItemMongoSnapshot> toReviewedItems(
			SubmittedItemGroup reviewedItemGroup,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return reviewedItemGroup.getSubmittedItems()
				.stream()
				.map(i -> ReviewedItemMongoSnapshot.of(i, reviewedProvenanceDataPointDocumentFactory))
				.collect(toList());
	}

	public static final class Builder {
		private String id;
		private String name;
		private List<ReviewedItemMongoSnapshot> reviewedItems;
		private boolean repeating;
		private String index;
		private String populatedItemGroupId;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withReviewedItems(List<ReviewedItemMongoSnapshot> reviewedItems) {
			this.reviewedItems = reviewedItems;
			return this;
		}

		public Builder withRepeating(boolean repeating) {
			this.repeating = repeating;
			return this;
		}

		public Builder withIndex(final String val) {
			index = val;
			return this;
		}

		public Builder withPopulatedItemGroupId(String populatedItemGroupId) {
			this.populatedItemGroupId = populatedItemGroupId;
			return this;
		}

		public ReviewedItemGroupMongoSnapshot build() {
			return new ReviewedItemGroupMongoSnapshot(this);
		}
	}
}

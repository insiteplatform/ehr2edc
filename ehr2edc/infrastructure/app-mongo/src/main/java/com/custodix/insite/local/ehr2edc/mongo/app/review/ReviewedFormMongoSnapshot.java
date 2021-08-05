package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ReviewedFormMongoSnapshot {
	private final String name;
	private final String formDefinitionId;
	private final List<ReviewedItemGroupMongoSnapshot> reviewedItemGroups;
	private final String populatedFormId;
	private final String localLab;

	@PersistenceConstructor
	ReviewedFormMongoSnapshot(String name, String formDefinitionId,
			List<ReviewedItemGroupMongoSnapshot> reviewedItemGroups, String populatedFormId, String localLab) {
		this.name = name;
		this.formDefinitionId = formDefinitionId;
		this.reviewedItemGroups = reviewedItemGroups;
		this.populatedFormId = populatedFormId;
		this.localLab = localLab;
	}

	private ReviewedFormMongoSnapshot(Builder builder) {
		name = builder.name;
		formDefinitionId = builder.formDefinitionId;
		reviewedItemGroups = builder.reviewedItemGroups;
		populatedFormId = builder.populatedFormId;
		localLab = builder.localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public String getFormDefinitionId() {
		return formDefinitionId;
	}

	public List<ReviewedItemGroupMongoSnapshot> getReviewedItemGroups() {
		return reviewedItemGroups;
	}

	public String getPopulatedFormId() {
		return populatedFormId;
	}

	public String getLocalLab() {
		return localLab;
	}

	public static ReviewedFormMongoSnapshot of(SubmittedForm reviewedForm,
                                               ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return ReviewedFormMongoSnapshot.newBuilder()
				.withName(reviewedForm.getName())
				.withFormDefinitionId(reviewedForm.getFormDefinitionId().getId())
				.withReviewedItemGroups(toReviewedItemGroupSnapshots(reviewedForm, reviewedProvenanceDataPointDocumentFactory))
				.withPopulatedFormId(reviewedForm.getPopulatedFormId().getId())
				.withLocalLab(reviewedForm.getLocalLab()
						.map(LabName::getName)
						.orElse(null))
				.build();
	}

	SubmittedForm toReviewedForm() {
		return SubmittedForm.newBuilder()
				.withName(name)
				.withFormDefinitionId(FormDefinitionId.of(formDefinitionId))
				.withSubmittedItemGroups(toMappedReviewedItemGroups())
				.withPopulatedFormId(FormId.of(populatedFormId))
				.withLocalLab(localLab == null ? null : LabName.of(localLab))
				.build();
	}

	private List<SubmittedItemGroup> toMappedReviewedItemGroups() {
		return reviewedItemGroups.stream()
				.map(ReviewedItemGroupMongoSnapshot::toReviewedItemGroup)
				.collect(toList());
	}

	private static List<ReviewedItemGroupMongoSnapshot> toReviewedItemGroupSnapshots(
			SubmittedForm reviewedForm,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return reviewedForm.getSubmittedItemGroups()
				.stream()
				.map(ig -> ReviewedItemGroupMongoSnapshot.of(ig, reviewedProvenanceDataPointDocumentFactory))
				.collect(toList());
	}

	public static final class Builder {
		private String formDefinitionId;
		private List<ReviewedItemGroupMongoSnapshot> reviewedItemGroups;
		private String populatedFormId;
		private String name;
		private String localLab;

		private Builder() {
		}

		public Builder withFormDefinitionId(String formDefinitionId) {
			this.formDefinitionId = formDefinitionId;
			return this;
		}

		public Builder withReviewedItemGroups(List<ReviewedItemGroupMongoSnapshot> reviewedItemGroups) {
			this.reviewedItemGroups = reviewedItemGroups;
			return this;
		}

		public Builder withPopulatedFormId(String populatedFormId) {
			this.populatedFormId = populatedFormId;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withLocalLab(final String val) {
			localLab = val;
			return this;
		}

		public ReviewedFormMongoSnapshot build() {
			return new ReviewedFormMongoSnapshot(this);
		}
	}
}

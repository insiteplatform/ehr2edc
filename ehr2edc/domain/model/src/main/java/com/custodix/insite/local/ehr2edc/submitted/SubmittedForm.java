package com.custodix.insite.local.ehr2edc.submitted;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class SubmittedForm {
	private final String name;
	private final FormDefinitionId formDefinitionId;
	private final List<SubmittedItemGroup> submittedItemGroups;
	private final FormId populatedFormId;
	private final LabName localLab;

	private SubmittedForm(Builder builder) {
		name = builder.name;
		formDefinitionId = builder.formDefinitionId;
		submittedItemGroups = builder.submittedItemGroups;
		populatedFormId = builder.populatedFormId;
		localLab = builder.localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public FormDefinitionId getFormDefinitionId() {
		return formDefinitionId;
	}

	public List<SubmittedItemGroup> getSubmittedItemGroups() {
		return submittedItemGroups;
	}

	public FormId getPopulatedFormId() {
		return populatedFormId;
	}

	public Optional<LabName> getLocalLab() {
		return Optional.ofNullable(localLab);
	}

	public List<UseCaseConstraintViolation> validateForSubmission() {
		Map<Key, List<ItemGroupId>> keyCounter = submittedItemGroups.stream()
				.filter(SubmittedItemGroup::hasKey)
				.collect(Collectors.groupingBy(Key::of,
						Collectors.mapping(SubmittedItemGroup::getPopulatedItemGroupId, Collectors.toList())));

		return keyCounter.entrySet()
				.stream()
				.filter(entry -> entry.getValue()
						.size() > 1)
				.flatMap(this::createUseCaseConstraintViolation)
				.collect(Collectors.toList());
	}

	SubmittedItemGroup getSubmittedItemGroup(ItemGroupDefinitionId itemGroupDefinitionId) {
		return submittedItemGroups.stream()
				.filter(g -> itemGroupDefinitionId.equals(g.getId()))
				.findFirst()
				.orElseThrow(() -> DomainException.of("study.submitted.itemgroup.definition.unknown",
						itemGroupDefinitionId.getId(), formDefinitionId.getId()));
	}

	SubmittedItem getSubmittedItem(ItemGroupDefinitionId itemGroupDefinitionId, ItemDefinitionId itemDefinitionId) {
		return getSubmittedItemGroup(itemGroupDefinitionId).getSubmittedItem(itemDefinitionId);
	}

	Optional<SubmittedItem> findSubmittedItem(SubmittedItemId submittedItemId) {
		return submittedItemGroups.stream()
				.map(g -> g.findSubmittedItem(submittedItemId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private Stream<UseCaseConstraintViolation> createUseCaseConstraintViolation(
			final Map.Entry<Key, List<ItemGroupId>> e) {
		return e.getValue()
				.stream()
				.map(toConstraintViolation(e));
	}

	private Function<ItemGroupId, UseCaseConstraintViolation> toConstraintViolation(
			Map.Entry<Key, List<ItemGroupId>> e) {
		return populatedItemGroupId -> UseCaseConstraintViolation.constraintViolation(populatedItemGroupId.getId(),
				String.format("%s: must be unique, found %s occurrences", errorPath(e.getKey()), e.getValue()
						.size()));
	}

	private String errorPath(Key key) {
		return String.format("Error in form with id \"%s\": ItemGroup with id \"%s\", repeat key \"%s\"",
				formDefinitionId.getId(), key.getItemGroupId()
						.getId(), String.join(",", key.getItemGroupKey()));
	}

	private static final class Key {
		private final ItemGroupDefinitionId itemGroupDefinitionId;
		private final List<String> itemGroupKey;

		private Key(final ItemGroupDefinitionId itemGroupDefinitionId, final List<String> itemGroupKey) {
			this.itemGroupDefinitionId = itemGroupDefinitionId;
			this.itemGroupKey = itemGroupKey;
		}

		private static Key of(SubmittedItemGroup reviewedItemGroup) {
			return new Key(reviewedItemGroup.getId(), reviewedItemGroup.getKey());
		}

		private ItemGroupDefinitionId getItemGroupId() {
			return itemGroupDefinitionId;
		}

		private List<String> getItemGroupKey() {
			return itemGroupKey;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final Key key = (Key) o;
			return itemGroupDefinitionId.equals(key.itemGroupDefinitionId) && itemGroupKey.equals(key.itemGroupKey);
		}

		@Override
		public int hashCode() {
			return Objects.hash(itemGroupDefinitionId, itemGroupKey);
		}
	}

	public static final class Builder {
		private String name;
		private FormDefinitionId formDefinitionId;
		private List<SubmittedItemGroup> submittedItemGroups;
		private FormId populatedFormId;
		private LabName localLab;

		private Builder() {
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withFormDefinitionId(FormDefinitionId formDefinitionId) {
			this.formDefinitionId = formDefinitionId;
			return this;
		}

		public Builder withSubmittedItemGroups(List<SubmittedItemGroup> submittedItemGroups) {
			this.submittedItemGroups = submittedItemGroups;
			return this;
		}

		public Builder withPopulatedFormId(FormId populatedFormId) {
			this.populatedFormId = populatedFormId;
			return this;
		}

		public Builder withLocalLab(LabName localLab) {
			this.localLab = localLab;
			return this;
		}

		public SubmittedForm build() {
			return new SubmittedForm(this);
		}
	}
}

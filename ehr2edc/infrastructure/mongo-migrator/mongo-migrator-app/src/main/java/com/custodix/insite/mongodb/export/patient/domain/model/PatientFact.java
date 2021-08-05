package com.custodix.insite.mongodb.export.patient.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public final class PatientFact {

	private final PatientIdentifier identifier;
	private final PatientGender gender;
	private final BirthInformation birthInformation;
	private final VitalStatus vitalStatus;
	private final DeathInformation deathInformation;

	private PatientFact(Builder builder) {
		identifier = builder.id;
		gender = builder.gender;
		birthInformation = builder.birthInformation;
		vitalStatus = builder.vitalStatus;
		deathInformation = builder.deathInformation;
	}

	public PatientIdentifier getIdentifier() {
		return identifier;
	}

	public PatientGender getGender() {
		return gender;
	}

	public BirthInformation getBirthInformation() {
		return birthInformation;
	}

	public VitalStatus getVitalStatus() {
		return vitalStatus;
	}

	public DeathInformation getDeathInformation() {
		return deathInformation;
	}

	private List<String> toDocumentStrings() {
		validateIdentifier();
		return Stream.of(gender, birthInformation, deathInformation, vitalStatus)
				.filter(Objects::nonNull)
				.map(fact -> fact.toDemographicDocumentFor(identifier.getSubjectId()))
				.collect(Collectors.toList());
	}

	public List<DemographicDocument> toDocuments() {
		validateIdentifier();
		return Stream.of(gender, birthInformation, deathInformation, vitalStatus)
				.filter(Objects::nonNull)
				.map(fact -> DemographicDocument.newBuilder()
						.withSubjectId(identifier.getSubjectId())
						.withDemographicType(fact.factType())
						.withValue(fact.factValue())
						.build())
				.collect(Collectors.toList());
	}

	private void validateIdentifier() {
		if (Objects.isNull(identifier) || Objects.isNull(identifier.getSubjectId()) || Objects.isNull(
				identifier.getSubjectId()
						.getId())) {
			throw new IllegalArgumentException("SubjectId is required for generation a Mongo Document.");
		}
	}

	@Override
	public String toString() {
		return "PatientFact{" + toDocumentStrings() + "}";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientIdentifier id;
		private PatientGender gender;
		private BirthInformation birthInformation;
		private DeathInformation deathInformation;
		private VitalStatus vitalStatus;

		private Builder() {
		}

		public Builder withIdentifier(PatientIdentifier val) {
			id = val;
			return this;
		}

		public Builder withGender(PatientGender val) {
			gender = val;
			return this;
		}

		public Builder withBirthInformation(BirthInformation val) {
			birthInformation = val;
			return this;
		}

		public Builder withVitalStatus(VitalStatus val) {
			vitalStatus = val;
			return this;
		}

		public Builder withDeathInformation(DeathInformation val) {
			deathInformation = val;
			return this;
		}

		public PatientFact build() {
			return new PatientFact(this);
		}
	}
}
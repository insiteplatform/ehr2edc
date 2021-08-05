package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class ModifierObjectMother {

	public static Modifier laterality() {
		return Modifier.newBuilder()
				.withReferenceCode("7771000")
				.withCategory(ModifierCategory.LATERALITY)
				.build();
	}

	public static Modifier position() {
		return Modifier.newBuilder()
				.withReferenceCode("33586001")
				.withCategory(ModifierCategory.POSITION)
				.build();
	}

	public static Modifier location() {
		return Modifier.newBuilder()
				.withReferenceCode("40983000")
				.withCategory(ModifierCategory.LOCATION)
				.build();
	}

}

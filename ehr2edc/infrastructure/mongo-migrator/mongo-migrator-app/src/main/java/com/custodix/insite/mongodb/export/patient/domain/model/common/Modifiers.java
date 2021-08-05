package com.custodix.insite.mongodb.export.patient.domain.model.common;

import java.util.List;
import java.util.Optional;

public final class Modifiers {
	private List<Modifier> modifierList;

	public Modifiers(List<Modifier> modifierList) {
		this.modifierList = modifierList;
	}

	public Optional<Modifier> getModifier(ModifierCategory category) {
		return modifierList.stream()
				.filter(m -> category == m.getCategory())
				.findFirst();
	}
}
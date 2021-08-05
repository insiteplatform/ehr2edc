package com.custodix.insite.mongodb.export.patient.domain.repository;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier;

public interface ModifierRepository {
	Optional<Modifier> getModifier(String modifierCode);
}

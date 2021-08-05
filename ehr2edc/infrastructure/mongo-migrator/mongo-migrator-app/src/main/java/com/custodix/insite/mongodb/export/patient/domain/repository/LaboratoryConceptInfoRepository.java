package com.custodix.insite.mongodb.export.patient.domain.repository;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo;

public interface LaboratoryConceptInfoRepository {
	Optional<LaboratoryConceptInfo> findByConcept(Concept concept);
}
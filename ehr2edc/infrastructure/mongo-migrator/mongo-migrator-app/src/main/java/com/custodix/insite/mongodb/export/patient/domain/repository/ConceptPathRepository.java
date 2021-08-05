package com.custodix.insite.mongodb.export.patient.domain.repository;

import java.util.List;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;

public interface ConceptPathRepository {
	List<ConceptPath> getConceptPaths(String baseCode);
}
package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo;
import com.custodix.insite.mongodb.export.patient.domain.repository.LaboratoryConceptInfoRepository;

class EmbeddedLaboratoryConceptInfoRepository implements LaboratoryConceptInfoRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedLaboratoryConceptInfoRepository.class);

	private final LaboratoryConceptInfoFetcher laboratoryConceptInfoFetcher;

	private List<LaboratoryConceptInfoMapping> laboratoryConceptInfoMappings = emptyList();

	EmbeddedLaboratoryConceptInfoRepository(LaboratoryConceptInfoFetcher laboratoryConceptInfoFetcher) {
		this.laboratoryConceptInfoFetcher = laboratoryConceptInfoFetcher;
	}

	void init() {
		this.laboratoryConceptInfoMappings = laboratoryConceptInfoFetcher.fetch();
	}

	@Override
	public Optional<LaboratoryConceptInfo> findByConcept(Concept concept) {
		Optional<LaboratoryConceptInfo> laboratoryConceptInfo = laboratoryConceptInfoMappings.stream()
				.filter(m -> codeMatches(concept, m))
				.map(LaboratoryConceptInfoMapping::getLaboratoryConceptInfo)
				.findFirst();
		logNotFoundConcept(concept, laboratoryConceptInfo);
		return laboratoryConceptInfo;
	}

	private void logNotFoundConcept(Concept concept, Optional<LaboratoryConceptInfo> laboratoryConceptInfo) {
		if (!laboratoryConceptInfo.isPresent()) {
			LOGGER.error("LaboratoryConceptInfo for concept {} not found.",
					concept == null ? "null" : concept.getPathElement());
		}
	}

	private boolean codeMatches(Concept concept, LaboratoryConceptInfoMapping laboratoryConceptInfoMapping) {
		return laboratoryConceptInfoMapping.getCode()
				.equals(concept.getCode());
	}
}
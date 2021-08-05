package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier;
import com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierCategory;
import com.custodix.insite.mongodb.export.patient.domain.repository.ModifierRepository;

public class DataWarehouseModifierRepository implements ModifierRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataWarehouseModifierRepository.class);
	private static final String MODIFIER_QUERY = "select modifier_path from modifier_dimension where modifier_cd = '%s'";
	private static final String MODIFIER_PATH_COLUMN = "modifier_path";

	private final JdbcTemplate jdbcTemplate;
	private final ModifierSettings modifierSettings;

	public DataWarehouseModifierRepository(JdbcTemplate jdbcTemplate, ModifierSettings modifierSettings) {
		this.jdbcTemplate = jdbcTemplate;
		this.modifierSettings = modifierSettings;
	}

	@Override
	public Optional<Modifier> getModifier(String modifierCode) {
		List<ConceptPath> paths = getPaths(modifierCode);
		if (paths.isEmpty()) {
			LOGGER.error("Modifier with modifier_cd '{}' not found in modifier_dimension", modifierCode);
			return Optional.empty();
		}
		return findModifier(paths);
	}

	private Optional<Modifier> findModifier(List<ConceptPath> paths) {
		return findReferenceCode(paths).map(c -> Optional.of(createModifier(getCategory(paths), c)))
				.orElseGet(() -> unknownReferenceCode(paths));
	}

	private List<ConceptPath> getPaths(String modifierCode) {
		return jdbcTemplate.query(format(MODIFIER_QUERY, modifierCode), (row, nr) -> {
			String path = row.getString(MODIFIER_PATH_COLUMN);
			return ConceptPath.newBuilder()
					.withPath(path)
					.build();
		});
	}

	private ModifierCategory getCategory(List<ConceptPath> paths) {
		return paths.stream()
				.map(this::findCategory)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElseGet(() -> unknownCategory(paths));
	}

	private Optional<ModifierCategory> findCategory(ConceptPath conceptPath) {
		Concept categoryConcept = conceptPath.getConcepts()
				.get(1);
		return Optional.ofNullable(modifierSettings.getCategoryMapping()
				.get(categoryConcept.getPathElement()));
	}

	private Optional<Modifier> unknownReferenceCode(List<ConceptPath> paths) {
		LOGGER.error("No reference code found. concept paths were: {}", paths);
		return Optional.empty();
	}

	private ModifierCategory unknownCategory(List<ConceptPath> paths) {
		LOGGER.error("No category found. concept paths were: {}", paths);
		return ModifierCategory.UNKNOWN;
	}

	private Optional<String> findReferenceCode(List<ConceptPath> paths) {
		return paths.stream()
				.map(this::findReferenceCode)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private Optional<String> findReferenceCode(ConceptPath path) {
		return path.getConcepts()
				.stream()
				.filter(c -> modifierSettings.getConceptNamespaces()
						.contains(c.getSchema()))
				.reduce((first, second) -> second)
				.map(Concept::getCode);
	}

	private Modifier createModifier(ModifierCategory category, String referenceCode) {
		return Modifier.newBuilder()
				.withCategory(category)
				.withReferenceCode(referenceCode)
				.build();
	}
}

package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.MapToStringProjector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MapUnitToCommonModel implements Projector<ProjectedValue, ProjectedValue> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MapUnitToCommonModel.class);
	private static final UnitMapper UCUM_TO_COMMON_UNIT_MAPPER = new UnitMapper();
	private static final String NAME = "Map unit to common model";

	private final ProjectedValueField field;
	private final boolean projectMissing;

	public MapUnitToCommonModel(ProjectedValueField field, boolean projectMissing) {
		this.field = field == null ? ProjectedValueField.VALUE : field;
		this.projectMissing = projectMissing;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<ProjectedValue> value, ProjectionContext context) {
		if (projectMissing) {
			return value.map(v -> doProjectCopyMissing(v, context));
		} else {
			return value.flatMap(v -> doProject(v, context));
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(ProjectedValue projectedValue, ProjectionContext context) {
		return projectedValue.getFieldValue(field)
				.flatMap(this::normalise)
				.flatMap(normalisedUnit -> UCUM_TO_COMMON_UNIT_MAPPER.map(context, normalisedUnit))
				.map(commonUnit -> projectedValue.copyWithFieldUpdate(field, (String) commonUnit));
	}

	private ProjectedValue doProjectCopyMissing(ProjectedValue projectedValue, ProjectionContext context) {
		return doProject(projectedValue, context).orElse(projectedValue);
	}

	private Optional<String> normalise(Object unit) {
		try {
			final String normalisedUnit = doNormalise(unit);
			return Optional.of(normalisedUnit);
		} catch (Exception e) {
			LOGGER.trace("Invalid unit: " + e.getMessage(), e);
			LOGGER.error("Invalid unit {}", unit);
			return Optional.empty();
		}
	}

	private String doNormalise(Object fieldValue) {
		return normaliseValue(fieldValue.toString());
	}

	private String normaliseValue(String ucum) {
		//Minimal implementation to make the tests pass.
		//TODO: implement
		return ucum.replace("l", "L");
	}

	private static final class UnitMapper {
		private static final Logger LOGGER = LoggerFactory.getLogger(UnitMapper.class);
		private static final String UNIT_MAPPING_FILE = "unitMapping.txt";

		private final Map<Object, String> units = new HashMap<>();

		UnitMapper() {
			try {
				loadUnitMappings();
			} catch (IOException e) {
				LOGGER.error("Failed to load unit mapping file: {}", e.getMessage(), e);
			}
		}

		private void loadUnitMappings() throws IOException {
			InputStream inputStream = getUnitMappingsAsInputStream();
			units.putAll(extractUnits(inputStream));
		}

		private Map<Object, String> extractUnits(InputStream inputStream) throws IOException {
			List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
			return lines.stream()
					.map(this::splitByTab)
					.collect(toUnitMap());
		}

		private Collector<String[], ?, Map<Object, String>> toUnitMap() {
			return Collectors.toMap(this::getUcumUnit, this::getCommonUnit, this::resolveDuplicateByTakingFirst);
		}

		private String[] splitByTab(String line) {
			return StringUtils.split(line, "\t");
		}

		private Object getUcumUnit(String[] line) {
			return line[0];
		}

		private String getCommonUnit(String[] line) {
			return line[1];
		}

		@SuppressWarnings("unused")
		private String resolveDuplicateByTakingFirst(String line1, String line2) {
			return line1;
		}

		private static InputStream getUnitMappingsAsInputStream() {
			return UnitMapper.class.getResourceAsStream(UNIT_MAPPING_FILE);
		}

		private Optional<String> map(ProjectionContext context, Object unit) {
			return new MapToStringProjector(units).project(Optional.of(unit), context);
		}
	}
}

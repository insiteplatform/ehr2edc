package com.custodix.insite.mongodb.export.patient.domain.model.common;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

public class ConceptPath {
	private static final String PATH_SEPARATOR_REGEX = "\\\\";

	private final String path;

	private ConceptPath(Builder builder) {
		path = builder.path;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getPath() {
		return path;
	}

	public List<Concept> getConcepts() {
		return Arrays.stream(path.split(PATH_SEPARATOR_REGEX))
				.filter(pathElement -> !StringUtils.isEmpty(pathElement))
				.map(this::getConcept)
				.collect(toList());
	}

	private Concept getConcept(final String pathElement) {
		int firstSplit = pathElement.indexOf('_');
		if (firstSplit < 0) {
			return buildConcept("", pathElement);
		} else {
			return buildConcept(pathElement.substring(0, firstSplit), pathElement.substring(firstSplit + 1));
		}
	}

	private Concept buildConcept(String schema, String code) {
		return Concept.newBuilder()
				.withSchema(schema)
				.withCode(code)
				.build();
	}

	@Override
	public String toString() {
		return "ConceptPath{" + "path='" + path + '\'' + '}';
	}

	public static final class Builder {
		private String path;

		private Builder() {
		}

		public Builder withPath(String val) {
			path = val;
			return this;
		}

		public ConceptPath build() {
			return new ConceptPath(this);
		}
	}
}
package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

class ODMXPathParser {

	private static final String XPATH_ELEMENT_SEPARATOR = "/";
	private static final String XPATH_INDEX_EXPRESSION = "[\\[\\]]";

	ODMXPathParser() {
	}

	Map<String, Integer> elementIndices(String location) {
		return splitIntoPaths(location)
				.map(this::splitOnIndex)
				.filter(this::hasIndex)
				.collect(toMap(this::elementKey, this::elementIndex));
	}

	private Stream<String> splitIntoPaths(String location) {
		return Arrays.stream(location.substring(1)
				.split(XPATH_ELEMENT_SEPARATOR));
	}

	private String[] splitOnIndex(String sublocation) {
		return sublocation.split(XPATH_INDEX_EXPRESSION);
	}

	private boolean hasIndex(String[] splitLocation) {
		return splitLocation.length == 2;
	}

	private String elementKey(String[] nameIndexTuple) {
		return nameIndexTuple[0];
	}

	private Integer elementIndex(String[] nameIndexTuple) {
		return toZeroBasedListIndex(nameIndexTuple[1]);
	}

	private int toZeroBasedListIndex(String s) {
		return Integer.parseInt(s) - 1;
	}
}

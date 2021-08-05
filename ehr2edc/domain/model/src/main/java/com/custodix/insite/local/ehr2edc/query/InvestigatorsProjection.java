package com.custodix.insite.local.ehr2edc.query;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface InvestigatorsProjection {
	Collection<InvestigatorProjection> getAssigned();


	default <T> Collection<T> map(Function<InvestigatorProjection, T> mapping) {
		return getAssigned().stream()
				.map(mapping)
				.collect(Collectors.toList());
	}
}

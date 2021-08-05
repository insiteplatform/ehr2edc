package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.util.Optional;

public interface Projector<T, P> {
	Optional<T> project(Optional<P> value, ProjectionContext context);

	String getName();
}

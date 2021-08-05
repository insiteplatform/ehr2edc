package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.util.List;
import java.util.Optional;

public interface QueryResult {

	List<DataPoint> getResults();

	default Optional<DataPoint> findResult() {
		return getResults().stream().findFirst();
	}

}

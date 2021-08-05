package com.custodix.insite.local.ehr2edc.query.fhir;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

class FhirEmptyResultGateway implements EHRGateway {
	private static final Logger LOGGER = getLogger(FhirEmptyResultGateway.class);

	@Override
	public boolean canHandle(Query query) {
		return true;
	}

	@Override
	public QueryResult execute(Query query, LocalDate referenceDate) {
		LOGGER.warn(String.format("Executing an FhirEmptyResultGateway for query '%s'", query.getClass().getName()));
		return new EmptyQueryResult();
	}

	class EmptyQueryResult implements QueryResult {

		@Override
		public List<DataPoint> getResults() {
			return Collections.emptyList();
		}
	}
}

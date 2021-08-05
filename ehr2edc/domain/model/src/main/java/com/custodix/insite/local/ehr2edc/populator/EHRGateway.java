package com.custodix.insite.local.ehr2edc.populator;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

public interface EHRGateway<T extends QueryResult, Q extends Query<T>> {

	boolean canHandle(Query<?> query);

	T execute(Q query, LocalDate referenceDate);
}

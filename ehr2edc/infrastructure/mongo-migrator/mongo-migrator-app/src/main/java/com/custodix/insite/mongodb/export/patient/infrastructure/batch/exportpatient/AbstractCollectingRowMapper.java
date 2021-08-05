package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Basic implementation of {@link CollectingRowMapper}.
 **/
public abstract class AbstractCollectingRowMapper<T> implements CollectingRowMapper<T> {

	private boolean lastNextResult;

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = mapRow(rs, null, rowNum);
		while (nextRow(rs) && isRelated(rs, result)) {
			result = mapRow(rs, result, ++rowNum);
		}
		return result;
	}

	/**
	 * Collects the current row into the given partial result.
	 * On the first call partialResult will be null, so this method must create
	 * an instance of T and map the row on it, on subsequent calls this method updates
	 * the previous partial result with data from the new row.
	 *
	 * @return The newly created (on the first call) or modified (on subsequent calls) partialResult.
	 **/
	protected abstract T mapRow(ResultSet rs, T partialResult, int rowNum) throws SQLException;

	/**
	 * Analyzes the current row to decide if it is related to previous ones.
	 * Typically it will compare some id on the current row with the one stored in the partialResult.
	 **/
	protected abstract boolean isRelated(ResultSet rs, T partialResult) throws SQLException;

	@Override
	public boolean hasNext() {
		return lastNextResult;
	}

	private boolean nextRow(ResultSet rs) throws SQLException {
		lastNextResult = rs.next();
		return lastNextResult;
	}
}
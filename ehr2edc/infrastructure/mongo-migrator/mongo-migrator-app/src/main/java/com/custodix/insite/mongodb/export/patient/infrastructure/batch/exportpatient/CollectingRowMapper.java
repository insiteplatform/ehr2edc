package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import org.springframework.jdbc.core.RowMapper;

/**
 * A RowMapper that collects data from more than one row to generate one result object.
 * This means that, unlike normal RowMapper, a CollectingRowMapper will call
 * <code>next()</code> on the given ResultSet until it finds a row that is not related
 * to previous ones.  Rows <b>must be sorted</b> so that related rows are adjacent.
 * Typically the T object will contain some single-value property (an id common
 * to all collected rows) and a Collection property.
 * <p/>
 * NOTE. Implementations will be stateful (to save the result of the last call
 * to <code>ResultSet.next()</code>), so <b>they cannot have singleton scope</b>.
 *
 * @see AbstractCollectingRowMapper
 **/
interface CollectingRowMapper<T> extends RowMapper<T> {
	/**
	 * Returns the same result of the last call to <code>ResultSet.next()</code> made by <code>RowMapper.mapRow(ResultSet, int)</code>.
	 * If <code>next()</code> has not been called yet, the result is meaningless.
	 **/
	boolean hasNext();
}
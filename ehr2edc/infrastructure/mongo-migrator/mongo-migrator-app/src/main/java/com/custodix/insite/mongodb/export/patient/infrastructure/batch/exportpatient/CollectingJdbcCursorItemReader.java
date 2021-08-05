package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.RowMapper;

/**
 * A JdbcCursorItemReader that uses a {@link CollectingRowMapper}.
 * Like the superclass this reader is not thread-safe.
 **/
class CollectingJdbcCursorItemReader<T> extends JdbcCursorItemReader<T> {

	private CollectingRowMapper<T> rowMapper;
	private boolean firstRead = true;

	/**
	 * Accepts a {@link CollectingRowMapper} only.
	 **/
	@Override
	public void setRowMapper(RowMapper<T> rowMapper) {
		this.rowMapper = (CollectingRowMapper<T>)rowMapper;
		super.setRowMapper(rowMapper);
	}

	/**
	 * Read next row and map it to item.
	 **/
	@Override
	protected T doRead() {
		if (rs == null) {
			throw new ReaderNotOpenException("Reader must be open before it can be read.");
		}
		try {
			if (firstRead) {
				if (!rs.next()) {  //Subsequent calls to next() will be executed by rowMapper
					return null;
				}
				firstRead = false;
			} else if (!rowMapper.hasNext()) {
				return null;
			}
			T item = readCursor(rs, getCurrentItemCount());
			return item;
		}
		catch (SQLException se) {
			throw getExceptionTranslator().translate("Attempt to process next row failed", getSql(), se);
		}
	}

	@Override
	protected T readCursor(ResultSet rs, int currentRow) throws SQLException {
		T result = super.readCursor(rs, currentRow);
		setCurrentItemCount(rs.getRow());
		return result;
	}
}
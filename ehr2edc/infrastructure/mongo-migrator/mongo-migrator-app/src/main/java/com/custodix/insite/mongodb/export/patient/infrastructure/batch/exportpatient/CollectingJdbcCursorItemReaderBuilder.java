package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

public final class CollectingJdbcCursorItemReaderBuilder<T> {
	private String name;
	private DataSource dataSource;
	private String sql;
	private PreparedStatementSetter preparedStatementSetter;
	private int fetchSize;
	private CollectingRowMapper<T> collectingRowMapper;

	public CollectingJdbcCursorItemReaderBuilder<T> withName(String name) {
		this.name = name;
		return this;
	}

	public CollectingJdbcCursorItemReaderBuilder<T> withDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public CollectingJdbcCursorItemReaderBuilder<T> withSql(String sql) {
		this.sql = sql;
		return this;
	}

	public CollectingJdbcCursorItemReaderBuilder<T> withQueryArguments(Object[] args) {
		this.preparedStatementSetter = new ArgumentPreparedStatementSetter(args);
		return this;
	}

	public CollectingJdbcCursorItemReaderBuilder<T> withFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
		return this;
	}

	public CollectingJdbcCursorItemReaderBuilder<T> withCollectingRowMapper(
			CollectingRowMapper<T> collectingRowMapper) {
		this.collectingRowMapper = collectingRowMapper;
		return this;
	}

	public CollectingJdbcCursorItemReader<T> build() {
		CollectingJdbcCursorItemReader<T> reader = new CollectingJdbcCursorItemReader<>();
		reader.setName(name);
		reader.setDataSource(dataSource);
		reader.setSql(sql);
		reader.setPreparedStatementSetter(preparedStatementSetter);
		reader.setFetchSize(fetchSize);
		reader.setRowMapper(collectingRowMapper);
		return reader;
	}
}

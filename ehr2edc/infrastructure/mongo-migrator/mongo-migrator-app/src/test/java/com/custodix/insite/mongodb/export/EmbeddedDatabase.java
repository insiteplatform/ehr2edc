package com.custodix.insite.mongodb.export;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.SocketUtils;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.SystemException;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;

class EmbeddedDatabase {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabase.class);
	private static final String USERNAME_PASS = "postgres";
	private static final List<String> INITIALIZING_DB_SCRIPTS = Arrays.asList("CONCEPT_DIMENSION_ehr.sql",
			"PATIENT_DIMENSION.sql", "OBSERVATION_FACT.sql", "PROVIDER_DIMENSION.sql", "VISIT_DIMENSION.sql",
			"MODIFIER_DIMENSION.sql", "PATIENT_MAPPING.sql", "ENCOUNTER_MAPPING.sql", "CODE_LOOKUP.sql");

	private static final List<String> PRE_INIT_DB_SCRIPTS = Collections.singletonList("init_db.sql");
	private static final List<String> INITIALIZING_DB_SCRIPTS_META = Collections.singletonList("metadata.sql");
	private static final List<String> REFRESH_DB_SCRIPTS = Arrays.asList("crc_create_query.sql", "test_queries.sql");
	private static final String METDATA_DB_NAME = "i2b2metadata";
	private static final String DEMODATA_DB_NAME = "i2b2demodata";

	private static EmbeddedDatabase embeddedDatabase;
	private final EmbeddedPostgres embeddedPostgres;
	private final String jdbcUrlMetaData;
	private final String jdbcUrlDemoData;

	private EmbeddedDatabase(final EmbeddedPostgres embeddedPostgres) {
		this.embeddedPostgres = embeddedPostgres;
		this.jdbcUrlMetaData = embeddedPostgres.getJdbcUrl(USERNAME_PASS, METDATA_DB_NAME);
		this.jdbcUrlDemoData = embeddedPostgres.getJdbcUrl(USERNAME_PASS, DEMODATA_DB_NAME);
	}

	static synchronized EmbeddedDatabase getEmbeddedDatabase() throws IOException {
		if (embeddedDatabase == null) {
			LOGGER.info("Creating embedded database.");
			embeddedDatabase = new EmbeddedDatabase(EmbeddedPostgres.builder()
					.setCleanDataDirectory(true)
					.setPort(SocketUtils.findAvailableTcpPort())
					.start());

			embeddedDatabase.addShutdownHookToStopDatabase();
			embeddedDatabase.intitializeDatabase(embeddedDatabase.getEmbeddedPostgres());

		}
		LOGGER.info("Database running");
		return embeddedDatabase;
	}

	private void addShutdownHookToStopDatabase() {
		Runtime.getRuntime()
				.addShutdownHook(new Thread(() -> {
					LOGGER.info("Shutting down database...");
					try {
						embeddedDatabase.getEmbeddedPostgres()
								.close();
					} catch (IOException e) {
						LOGGER.error("Shutting down database... failed");
					}
					LOGGER.info("Database shutdown");
				}));
	}

	private String resourcePath(final String localResourceFileName) {
		return EmbeddedDatabase.class.getPackage()
				.getName()
				.replace('.', '/')
				.concat("/")
				.concat(localResourceFileName);
	}

	private void intitializeDatabase(EmbeddedPostgres embeddedPostgres) {
		initializeUsersAndSchemas(embeddedPostgres);
		initializeI2B2DemoDatabase();
		initializeMetaDatabase();
	}

	private void initializeMetaDatabase() {
		DataSource dataSourceMeta = buildDataSource(METDATA_DB_NAME, METDATA_DB_NAME, getJdbcUrlMetaData());
		executeFlywayMigration(dataSourceMeta, "classpath:db_migration/postgresql/ont/create_1_0",
				"classpath:db_migration/postgresql/ont/migrations");
		try (Connection conn = dataSourceMeta.getConnection()) {
			executeScripts(INITIALIZING_DB_SCRIPTS_META, conn);
		} catch (SQLException e) {
			handleSQLException(e);
		}

	}

	private void executeFlywayMigration(final DataSource dataSource, String... flywayLocations) {
		Flyway flyway = new Flyway();
		flyway.setLocations(flywayLocations);
		flyway.setDataSource(dataSource);
		flyway.migrate();
	}

	private void initializeI2B2DemoDatabase() {
		DataSource dataSource = buildDataSource(DEMODATA_DB_NAME, DEMODATA_DB_NAME, getJdbcUrlDemoData());
		executeFlywayMigration(dataSource, "classpath:db_migration/postgresql/crc/create_1_0",
				"classpath:db_migration/postgresql/crc/migrations");

		try (Connection conn = dataSource.getConnection()) {
			executeScripts(INITIALIZING_DB_SCRIPTS, conn);
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.setSeparator(";;");
			populator.setScripts(new ClassPathResource(resourcePath("functions.sql")));
			populator.populate(conn);

		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	private DataSource buildDataSource(String username, String password, String url) {
		return new DriverManagerDataSource(url, username, password);
	}

	private void initializeUsersAndSchemas(final EmbeddedPostgres embeddedPostgres) {
		try (Connection conn = embeddedPostgres.getPostgresDatabase()
				.getConnection()) {
			executeScripts(PRE_INIT_DB_SCRIPTS, conn);
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	private void handleSQLException(final SQLException e) {
		throw new SystemException(e.getMessage(), e);
	}

	void refreshDatabase() {
		try (Connection conn = buildDataSource(DEMODATA_DB_NAME, DEMODATA_DB_NAME,
				getJdbcUrlDemoData()).getConnection()) {
			executeScripts(REFRESH_DB_SCRIPTS, conn);
		} catch (SQLException e) {
			handleSQLException(e);
		}

	}

	private void executeScripts(final List<String> scripts, final Connection conn) {
		for (String script : scripts) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource(resourcePath(script)));
		}
	}

	String getJdbcUrlMetaData() {
		return jdbcUrlMetaData;
	}

	String getJdbcUrlDemoData() {
		return jdbcUrlDemoData;
	}

	private EmbeddedPostgres getEmbeddedPostgres() {
		return embeddedPostgres;
	}
}

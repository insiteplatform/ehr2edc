package com.custodix.CDW.installer;

import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

public class Install {

	public static void main(String[] args) throws SQLException {
		printPostgresUpdateHintOnUsage();

		if ("-h".equals(args[0])) {
			printUsage();
		} else if ("-cdb".equals(args[0])) {
			Config conf = new CommandLineUI().collectConfiguration();
			System.out.println("DB creation started. Please wait...");
			try {
				create(conf, "crc");
				create(conf, "ont");
				System.out.println("DB creation completed");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("DB creation failed");
			}
		} else if ("-udb".equals(args[0])) {
			Config conf = new CommandLineUI().collectUpdateConfiguration();
			System.out.println("DB update started. Please wait...");
			try {
				update(conf, "crc");
				update(conf, "ont");
				System.out.println("DB update completed");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("DB update failed");
			}
		} else if ("-mdb".equals(args[0])) {
			Config conf = new CommandLineUI().collectConfiguration();
			System.out.println("DB migration started. Please wait...");
			try {
				migrate(conf, "crc");
				migrate(conf, "ont");
				System.out.println("DB migration completed");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("DB migration failed");
			}
		} else {
			System.out.println("Unknown command");
			printUsage();
		}
	}

	private static void printPostgresUpdateHintOnUsage() {
		System.out.println("===============================================");
		System.out.println("\n\nThis tool has been modified to support clean installs on postgres 12.2");
		System.out.println("This has a potential side-effect on 9.4 that the checksum of existing migrations might be different");
		System.out.println("To run this tool on existing installs, set the baseline property: ");
		System.out.println("===============================================");
	}

	private static void create(Config conf, String i2b2Database) throws SQLException {
		migrate(conf, i2b2Database);
	}

	private static void update(Config conf, String i2b2Database) throws SQLException {
		Flyway flyway = initializeFlyway(conf, i2b2Database);
		flyway.setLocations(migrationLocation(conf.getProperty(i2b2Database + ".vendor").get(), i2b2Database));
		flyway.setBaselineVersion(MigrationVersion.fromVersion(conf.getProperty(i2b2Database + ".baseline").get()));
		flyway.baseline();
		flyway.migrate();
	}

	private static void migrate(Config conf, String i2b2Database) throws SQLException {
		Flyway flyway = initializeFlyway(conf, i2b2Database);
		flyway.setLocations(creationLocation(conf.getProperty(i2b2Database + ".vendor").get(), i2b2Database),
				migrationLocation(conf.getProperty(i2b2Database + ".vendor").get(), i2b2Database));
		flyway.migrate();
	}

	private static Flyway initializeFlyway(Config conf, String i2b2Database) {
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:" + conf.getProperty(i2b2Database + ".vendor").get() + "://"
				+ conf.getProperty(i2b2Database + ".host").get() + ":" + conf.getProperty(i2b2Database + ".port").get()
				+ "/" + conf.getProperty(i2b2Database + ".database").get(),
				conf.getProperty(i2b2Database + ".user").get(), conf.getProperty(i2b2Database + ".password").get());
		flyway.setTable("schema_version_" + i2b2Database);
		return flyway;
	}

	private static String creationLocation(String databaseType, String i2b2DB) {
		return "classpath:db_migration/" + databaseType + "/" + i2b2DB + "/create_1_0";
	}

	private static String migrationLocation(String databaseType, String i2b2DB) {
		return "classpath:db_migration/" + databaseType + "/" + i2b2DB + "/migrations";
	}

	private static void printUsage() {
		System.out.println("Available options (only one of the following):");
		System.out.println("\t-h, --help: Usage information");
		System.out.println("\t-cdb, Create Database: Creates all required DB objects in an empty DB schema.");
		System.out.println(
				"\t-udb, Update Database: Updates an existing non-flyway managed database with a known version.");
		System.out.println("\t-mdb, Migrate Database: Starting from a flyway managed database.");
	}
}

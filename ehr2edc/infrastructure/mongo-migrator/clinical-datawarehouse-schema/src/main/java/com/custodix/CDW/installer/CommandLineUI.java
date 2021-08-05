package com.custodix.CDW.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandLineUI {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private static final List<String> DATABASEOPTIONS = new ArrayList<>(Arrays.asList("postgresql"));
	Config config;

	public CommandLineUI() {
		this.config = new Config();
	}

	public Config collectConfiguration() {
		do {
			collectConfiguration("crc", "demodata");
			collectConfiguration("ont", "metadata");
		} while (!validateDifferentDatabases("crc", "ont"));
		return config;
	}

	private void collectConfiguration(String cellName, String alternativeName) {
		String displayName = createDisplayName(cellName, alternativeName);
		System.out.println("CONFIGURATION FOR " + displayName + "\n");
		do {
			collectOption("Please choose the database type of the " + displayName + " database server:",
					DATABASEOPTIONS, cellName + ".vendor");
			collectParameter("What is the hostname of the " + displayName + " database server?", cellName + ".host");
			collectParameter("What is the port of the " + displayName + " database server?", cellName + ".port");
			collectParameter("What is the name of the " + displayName + " database server?", cellName + ".database");
		} while (!requestAcknowledgement(cellName));
		collectParameter("What is the username to connect to the " + displayName + " database server?",
				cellName + ".user");
		collectSecret("What is the password to connect to the " + displayName + " database server?",
				cellName + ".password");

	}

	public Config collectUpdateConfiguration() {
		do {
			collectMigrationConfiguration("crc", "demodata");
			collectMigrationConfiguration("ont", "metadata");
		} while (!validateDifferentDatabases("crc", "ont"));
		return config;
	}

	private void collectMigrationConfiguration(String cellName, String alternativeName) {
		collectConfiguration(cellName, alternativeName);
		collectParameter(
				"What is the current version of " + createDisplayName(cellName, alternativeName) + " database server?",
				cellName + ".baseline");
	}

	private String createDisplayName(String cellName, String alternativeName) {
		return cellName + " (" + alternativeName + ")";

	}

	private boolean requestAcknowledgement(String i2b2Database) {
		String toAcknowledge = "Please check the DB connection properties before you continue\n\tConnection string: "
				+ generateURL(i2b2Database) + "\nAre these properties correct? ([y], n)";
		System.out.println(toAcknowledge);
		try {
			String input = br.readLine();
			if (input.isEmpty() || input.equals("y")) {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean validateDifferentDatabases(String db1, String db2) {
		if (!generateURL(db1).equals(generateURL(db2))) {
			return true;
		} else {
			System.out.println("CRC and ONT databases should be different:\n\tCRC: " + generateURL(db1) + "\n\tONT: "
					+ generateURL(db2) + "\nPlease revise the ONT properties\n");
			return false;
		}
	}

	private String generateURL(String i2b2Database) {
		return "jdbc:" + config.getProperty(i2b2Database + ".vendor").get() + "://"
				+ config.getProperty(i2b2Database + ".host").get() + ":"
				+ config.getProperty(i2b2Database + ".port").get() + "/"
				+ config.getProperty(i2b2Database + ".database").get();
	}

	private void collectOption(String question, List<String> options, String property) {
		String prompt = question;
		int index = 1;
		for (String option : options) {
			prompt += "\n\t" + index++ + ". " + option;
		}

		String input = null;
		do {

			System.out.println(prompt);
			try {
				input = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!validateChoice(options, property, input));
	}

	private boolean validateChoice(List<String> options, String property, String input) {
		if (isValidChoice(options, input)) {
			config.setProperty(property, options.get(Integer.parseInt(input) - 1));
			return true;
		} else {
			System.out.println("WARNING: Wrong option selected.");
			return false;
		}
	}

	private boolean isValidChoice(List<String> options, String input) {
		if (input.isEmpty()) {
			return false;
		}
		if (!isWholeNumber(input)) {
			return false;
		}
		if (Integer.parseInt(input) > options.size() || Integer.parseInt(input) < 1) {
			return false;
		}
		return true;
	}

	private boolean isWholeNumber(String input) {
		return input.matches("^[0-9]+$");
	}

	private void collectParameter(String question, String name) {
		String prompt = question;

		Optional<String> property = config.getProperty(name);
		if (property.isPresent()) {
			prompt += " [" + property.get() + "]";
		}
		System.out.println(prompt);

		try {
			String input = br.readLine();
			if (!input.isEmpty()) {
				config.setAndCacheProperty(name, input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void collectSecret(String question, String name) {
		System.out.println(question);

		try {
			String input = br.readLine();
			config.setProperty(name, input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

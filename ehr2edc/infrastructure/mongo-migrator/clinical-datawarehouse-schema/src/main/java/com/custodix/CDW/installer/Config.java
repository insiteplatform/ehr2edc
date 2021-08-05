package com.custodix.CDW.installer;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class Config {
	private Properties secretProperties = new Properties();
	private Properties properties = new Properties();

	private final String filename;

	public Config() {
		File cache = new File(System.getProperty("user.home"), "cache.properties");
		filename = cache.getAbsolutePath();

		if (cache.exists()) {
			try (InputStream input = new FileInputStream(filename)) {
				properties.load(input);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}

	public Optional<String> getProperty(String name) {
		Optional<String> prop = Optional.ofNullable(properties.getProperty(name));
		if (!prop.isPresent()) {
			prop = Optional.ofNullable(secretProperties.getProperty(name));
		}
		return prop;
	}

	public void setAndCacheProperty(String name, String value) {
		properties.setProperty(name, value);
		cache();
	}

	public void setProperty(String name, String value) {
		secretProperties.setProperty(name, value);
	}

	private void cache() {
		try (OutputStream output = new FileOutputStream(filename)) {
			properties.store(output, null);
		} catch (IOException e) {
			System.out.println("Cannot write cache.properties: " + e.getMessage());
		}
	}
}
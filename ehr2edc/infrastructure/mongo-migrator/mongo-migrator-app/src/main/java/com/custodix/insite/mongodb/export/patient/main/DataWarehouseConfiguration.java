package com.custodix.insite.mongodb.export.patient.main;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@EnableConfigurationProperties(DataWarehouseSettings.class)
public class DataWarehouseConfiguration {
	@Bean
	public DataSource dataSource(DataWarehouseSettings dataWarehouseSettings) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(dataWarehouseSettings.getDriverClassName());
		dataSource.setUrl(dataWarehouseSettings.getUrl());
		dataSource.setUsername(dataWarehouseSettings.getUsername());
		dataSource.setPassword(dataWarehouseSettings.getPassword());

		return dataSource;
	}
}
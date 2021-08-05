package com.custodix.insite.local.ehr2edc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.custodix.insite.local.ehr2edc.config.EHR2EDCConfig;
import com.custodix.insite.local.ehr2edc.infra.users.UsersConfiguration;
import com.custodix.insite.local.ehr2edc.jms.LocalJMSConfiguration;
import com.custodix.insite.local.ehr2edc.web.EtagConfiguration;
import com.custodix.insite.mongodb.export.patient.event.config.MongoMigratorEventConfiguration;
import com.custodix.insite.mongodb.export.patient.main.DataWarehouseConfiguration;

import eu.ehr4cr.workbench.local.eventpublisher.EventPublisherConfiguration;

@SpringBootApplication
@Import({ EHR2EDCConfig.class,
		  DataWarehouseConfiguration.class,
		  LocalJMSConfiguration.class,
		  EventPublisherConfiguration.class,
		  MongoMigratorEventConfiguration.class,
		  UsersConfiguration.class,
		  EtagConfiguration.class })
@ServletComponentScan(basePackages = { "com.custodix" })
@PropertySources({ @PropertySource("${application.credentials.location:classpath:credentials.properties}"),
				   @PropertySource("classpath:ehr2edc-infra-users.properties"),
				   @PropertySource("${ehr2edc-infra-users.config.file}") })
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class SpringBootEHR2EDC extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootEHR2EDC.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEHR2EDC.class, args);
	}

}

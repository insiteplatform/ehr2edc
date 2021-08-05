package com.custodix.local.workbench.configuration;

import com.custodix.insite.local.configuration.LocalWorkBenchSpringConfiguration;
import com.custodix.insite.local.ehr2edc.config.EHR2EDCConfig;
import com.custodix.insite.local.ehr2edc.event.config.EHR2EDCEventConfiguration;
import com.custodix.insite.mongodb.export.patient.event.config.MongoMigratorEventConfiguration;
import com.custodix.workbench.local.mail.MailConfiguration;
import com.custodix.workbench.local.scheduling.SchedulingConfiguration;
import com.custodix.workbench.local.time.TimeConfiguration;
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

@SpringBootApplication
@Import({ MailConfiguration.class,
		  SchedulingConfiguration.class,
		  TimeConfiguration.class,
		  WebConfiguration.class,
		  LocalWorkBenchSpringConfiguration.class,
		  EHR2EDCConfig.class,
		  EHR2EDCEventConfiguration.class,
		  MongoMigratorEventConfiguration.class
		})
@ServletComponentScan(basePackages = { "com.custodix", "eu.ehr4cr.workbench" })
@PropertySources({ @PropertySource("classpath:lwb.properties"),
				   @PropertySource("${lwb.config.file}"),
				   @PropertySource("classpath:lwb-infra.properties"),
				   @PropertySource("${lwb-infra.config.file}"),
				   @PropertySource("${application.credentials.location:classpath:credentials.properties}")
				 })
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class SpringBootLocalWorkBench extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootLocalWorkBench.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootLocalWorkBench.class, args);
	}

}

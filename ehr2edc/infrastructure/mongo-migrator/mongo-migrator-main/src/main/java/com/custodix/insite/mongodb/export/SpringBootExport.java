package com.custodix.insite.mongodb.export;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration;

@SpringBootApplication
@EnableAsync
@ComponentScan(excludeFilters = @Filter(type = ASSIGNABLE_TYPE,
										classes = PatientCommandLineExporter.class))
@Import({ ExportPatientRunnerConfiguration.class })
@PropertySource("ehr2edc-infra-mongo-migrator.properties")
public class SpringBootExport {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExport.class, args);
	}

}
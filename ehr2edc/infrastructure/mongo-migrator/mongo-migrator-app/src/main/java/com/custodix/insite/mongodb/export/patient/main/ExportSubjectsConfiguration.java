package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects;
import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects;
import com.custodix.insite.mongodb.export.patient.application.command.ExportSubjectsCommand;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportsubjects.ExportSubjectsRunner;

@Configuration
public class ExportSubjectsConfiguration {
	@Bean
	ExportSubjects exportSubjects(ExportSubjectsRunner exportSubjectsRunner) {
		return new ExportSubjectsCommand(exportSubjectsRunner);
	}

	@Bean
	public ExportSubjectsRunner exportSubjectsRunner(GetActiveSubjects getActiveSubjects, ExportPatient exportPatient) {
		return new ExportSubjectsRunner(getActiveSubjects, exportPatient);
	}
}

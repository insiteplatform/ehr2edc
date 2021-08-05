package com.custodix.insite.mongodb.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient.Request;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@SpringBootApplication(scanBasePackages = "com.custodix.insite.mongodb")
@PropertySource("ehr2edc-infra-mongo-migrator.properties")
public class PatientCommandLineExporter implements CommandLineRunner {
	private static final String DEFAULT_PATIENT_ID = "1";
	private static final String DEFAULT_NAMESPACE = "test";
	private static final String DEFAULT_SUBJECT = "test-subject";

	@Autowired
	private ExportPatient exportPatient;
	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(PatientCommandLineExporter.class, args);
	}

	@Override
	public void run(String... args) {
		Request request = buildRequest();
		exportPatient.export(request);
	}

	private Request buildRequest() {
		String patientId = environment.getProperty("patientId", DEFAULT_PATIENT_ID);
		String namespace = environment.getProperty("namespace", DEFAULT_NAMESPACE);
		String subject = environment.getProperty("subject", DEFAULT_SUBJECT);
		return Request.newBuilder()
				.withPatientIdentifier(PatientIdentifier.of(PatientId.of(patientId), Namespace.of(namespace), SubjectId.of(subject)))
				.build();
	}
}
package com.custodix.insite.mongodb.export.patient.infrastructure.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;

import org.junit.Test;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExportPatientControllerUnitTest {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void canDeserializeRequest() throws IOException {
		String patientId = "0001012001";
		String namespace = "MASTER_PATIENT_INDEX";
		String subjectId = "SUBJ";
		String sampleRequest =
				"{\"patientIdentifier\":{\"patientId\":{\"id\":\"" + patientId + "\"}," + "\"namespace\":{\"name\":\""
						+ namespace + "\"},\"subjectId\":\"" + subjectId + "\"}}";

		final ExportPatient.Request request = objectMapper.readValue(sampleRequest, ExportPatient.Request.class);

		assertThat(request.getPatientIdentifier()
				.getPatientId()
				.getId(), equalTo(patientId));
		assertThat(request.getPatientIdentifier()
				.getNamespace()
				.getName(), equalTo(namespace));
		assertThat(request.getPatientIdentifier()
				.getSubjectId().getId(), equalTo(subjectId));
	}

}
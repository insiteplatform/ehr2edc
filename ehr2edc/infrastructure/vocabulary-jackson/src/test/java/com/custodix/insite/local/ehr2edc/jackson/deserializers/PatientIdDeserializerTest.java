package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.custodix.insite.local.ehr2edc.jackson.VocabularyModule;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientIdDeserializerTest {
	private static final String PATIENT_SOURCE = "source-1";

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(VocabularyModule.create());
	}

	@Test
	public void normalDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("patient-1");
		PatientCDWReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedPatientId("patient-1"));
	}

	@Test
	public void leadingWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("  patient-1");
		PatientCDWReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedPatientId("patient-1"));
	}

	@Test
	public void trailingWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("patient-1  ");
		PatientCDWReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedPatientId("patient-1"));
	}

	@Test
	public void middleWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("patient 1");
		PatientCDWReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedPatientId("patient 1"));
	}

	private String buildSerializedPatientId(String patientId) {
		return String.format("{ \"id\": \"%s\", \"source\": \"%s\" }", patientId, PATIENT_SOURCE);
	}

	private PatientCDWReference readPatientId(String serialized) throws IOException {
		return objectMapper.readValue(serialized, PatientCDWReference.class);
	}

	private PatientCDWReference buildExpectedPatientId(String patientId) {
		return PatientCDWReference.newBuilder()
				.withId(patientId)
				.withSource(PATIENT_SOURCE)
				.build();
	}
}
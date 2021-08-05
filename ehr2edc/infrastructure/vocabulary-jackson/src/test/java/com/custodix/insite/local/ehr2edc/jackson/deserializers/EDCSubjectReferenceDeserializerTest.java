package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.custodix.insite.local.ehr2edc.jackson.VocabularyModule;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EDCSubjectReferenceDeserializerTest {
	private static final String PATIENT_SOURCE = "source-1";

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(VocabularyModule.create());
	}

	@Test
	public void normalDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("subject-1");
		EDCSubjectReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedSubjectId("subject-1"));
	}

	@Test
	public void leadingWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("  subject-1");
		EDCSubjectReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedSubjectId("subject-1"));
	}

	@Test
	public void trailingWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("subject-1  ");
		EDCSubjectReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedSubjectId("subject-1"));
	}

	@Test
	public void middleWhitespaceDeserialization() throws IOException {
		String serialized = buildSerializedPatientId("subject 1");
		EDCSubjectReference deserialized = readPatientId(serialized);
		assertThat(deserialized).isEqualTo(buildExpectedSubjectId("subject 1"));
	}

	private String buildSerializedPatientId(String subjectId) {
		return String.format("\"%s\"", subjectId);
	}

	private EDCSubjectReference readPatientId(String serialized) throws IOException {
		return objectMapper.readValue(serialized, EDCSubjectReference.class);
	}

	private EDCSubjectReference buildExpectedSubjectId(String subjectId) {
		return EDCSubjectReference.newBuilder()
				.withId(subjectId)
				.build();
	}
}
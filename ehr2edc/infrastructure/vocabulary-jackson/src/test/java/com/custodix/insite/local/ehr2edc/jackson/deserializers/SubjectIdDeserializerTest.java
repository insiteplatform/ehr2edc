package com.custodix.insite.local.ehr2edc.jackson.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.custodix.insite.local.ehr2edc.jackson.VocabularyModule;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubjectIdDeserializerTest {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(VocabularyModule.create());
	}

	@Test
	public void normalDeserialization() throws IOException {
		String serialized = "\"subject-1\"";
		SubjectId deserialized = readSubjectId(serialized);
		assertThat(deserialized).isEqualTo(SubjectId.of("subject-1"));
	}

	@Test
	public void leadingWhitespaceDeserialization() throws IOException {
		String serialized = "\"  subject-1\"";
		SubjectId deserialized = readSubjectId(serialized);
		assertThat(deserialized).isEqualTo(SubjectId.of("subject-1"));
	}

	@Test
	public void trailingWhitespaceDeserialization() throws IOException {
		String serialized = "\"subject-1  \"";
		SubjectId deserialized = readSubjectId(serialized);
		assertThat(deserialized).isEqualTo(SubjectId.of("subject-1"));
	}

	@Test
	public void middleWhitespaceDeserialization() throws IOException {
		String serialized = "\"subject 1\"";
		SubjectId deserialized = readSubjectId(serialized);
		assertThat(deserialized).isEqualTo(SubjectId.of("subject 1"));
	}

	private SubjectId readSubjectId(String serialized) throws IOException {
		return objectMapper.readValue(serialized, SubjectId.class);
	}
}
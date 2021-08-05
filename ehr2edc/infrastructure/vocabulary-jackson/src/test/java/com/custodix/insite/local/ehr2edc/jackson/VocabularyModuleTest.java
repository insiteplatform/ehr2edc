package com.custodix.insite.local.ehr2edc.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VocabularyModuleTest {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(VocabularyModule.create());
	}

	@Test
	public void testMappingOfSubjectId() throws JsonProcessingException {
		SubjectId subjectId = SubjectId.of("subjectId");
		assertThat(asJson(new DummyJsonClass<>(subjectId))).isEqualTo(getResultingStringJson(subjectId.getId()));
	}

	@Test
	public void testMappingOfStudyId() throws JsonProcessingException {
		StudyId studyId = StudyId.of("studyId");
		assertThat(asJson(new DummyJsonClass<>(studyId))).isEqualTo(getResultingStringJson(studyId.getId()));
	}

	@Test
	public void testMappingOfUserId() throws JsonProcessingException {
		UserIdentifier userIdentifier = UserIdentifier.of("1");
		assertThat(asJson(new DummyJsonClass<>(userIdentifier))).isEqualTo(
				getResultingStringJson(userIdentifier.getId()));
	}

	@Test
	public void testMappingOfEventDefinitionId() throws JsonProcessingException {
		EventDefinitionId eventDefinitionId = EventDefinitionId.of("eventDefinitionId");

		assertSerializationBothWays(eventDefinitionId);
		assertThat(asJson(new DummyJsonClass<>(eventDefinitionId))).isEqualTo(getResultingStringJson(eventDefinitionId.getId()));
	}

	@Test
	public void testMappingOfSubmittedItemId() throws JsonProcessingException {
		SubmittedItemId submittedItemId = SubmittedItemId.of("submittedItemId");

		assertSerializationBothWays(submittedItemId);
		assertThat(asJson(new DummyJsonClass<>(submittedItemId))).isEqualTo(getResultingStringJson(submittedItemId.getId()));
	}

	private String asJson(DummyJsonClass dummyJsonClass) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dummyJsonClass);
	}

	private String getResultingStringJson(String result) {
		return "{\"topLvlObject\":\"" + result + "\"}";
	}

	private <T> void assertSerializationBothWays(T o) {
		try {
			assertThat(objectMapper.readValue(objectMapper.writeValueAsString(o),
					o.getClass())).isEqualToComparingFieldByFieldRecursively(o);
		} catch (IOException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	class DummyJsonClass<T> {
		private final T topLvlObject;

		DummyJsonClass(T topLvlObject) {
			this.topLvlObject = topLvlObject;
		}

		public T getTopLvlObject() {
			return topLvlObject;
		}
	}
}
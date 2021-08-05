package com.custodix.insite.local.ehr2edc.rest.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonWebConfigurationTest {
	private static final String INSTANT_STRING = "1992-06-14T13:50:30.000000020Z";
	private static final Instant INSTANT = Instant.parse(INSTANT_STRING);
	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		new JacksonWebConfiguration(objectMapper);
	}

	@Test
	public void testMappingOfInstant() throws JsonProcessingException {
		assertThat(asJson(new DummyJsonClass<>(INSTANT))).isEqualTo(getResultingStringJson(INSTANT_STRING));
	}

	private String asJson(DummyJsonClass dummyJsonClass) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dummyJsonClass);
	}

	private String getResultingStringJson(String result) {
		return "{\"topLvlObject\":\"" + result + "\"}";
	}

	static class DummyJsonClass<T> {
		private final T topLvlObject;

		DummyJsonClass(T topLvlObject) {
			this.topLvlObject = topLvlObject;
		}

		public T getTopLvlObject() {
			return topLvlObject;
		}
	}
}
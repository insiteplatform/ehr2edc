package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class OpenClinicaGetSubjectsResponseHandler {
	private final ObjectMapper objectMapper;

	OpenClinicaGetSubjectsResponseHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	List<EDCSubjectReference> handleResponse(String body) throws IOException {
		GetSubjectsResponse response = objectMapper.readValue(body, GetSubjectsResponse.class);
		return response.getStudyParticipants()
				.stream()
				.map(StudyParticipant::getSubjectKey)
				.map(EDCSubjectReference::of)
				.collect(Collectors.toList());
	}

	private static final class GetSubjectsResponse {
		private List<StudyParticipant> studyParticipants;

		public List<StudyParticipant> getStudyParticipants() {
			return studyParticipants;
		}
	}

	private static final class StudyParticipant {
		private String subjectKey;

		public String getSubjectKey() {
			return subjectKey;
		}
	}
}

package com.custodix.insite.local.ehr2edc.rest.study;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.ListEventDefinitions;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/events")
public final class EventDefinitionsController {

	private final ListEventDefinitions listEventDefinitions;

	EventDefinitionsController(ListEventDefinitions listEventDefinitions) {
		this.listEventDefinitions = listEventDefinitions;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ListEventDefinitions.Response listEventDefinitions(@PathVariable String studyId,
			@PathVariable String subjectId) {
		return listEventDefinitions.list(ListEventDefinitions.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withSubjectId(SubjectId.of(subjectId))
				.build());
	}
}

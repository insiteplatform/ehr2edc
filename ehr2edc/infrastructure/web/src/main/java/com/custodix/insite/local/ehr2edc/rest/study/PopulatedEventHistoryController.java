package com.custodix.insite.local.ehr2edc.rest.study;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory;
import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory.Request;
import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory.Response;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/populated/history")
public final class PopulatedEventHistoryController {

	private final GetPopulatedEventHistory getPopulatedEventHistory;

	public PopulatedEventHistoryController(GetPopulatedEventHistory getPopulatedEventHistory) {
		this.getPopulatedEventHistory = getPopulatedEventHistory;
	}

	@GetMapping
	public Response getPopulatedEventHistory(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @RequestParam EventDefinitionId eventDefinitionId) {
		Request request = buildRequest(subjectId, eventDefinitionId);
		return getPopulatedEventHistory.get(request);
	}

	private Request buildRequest(SubjectId subjectId, EventDefinitionId eventDefinitionId) {
		return Request.newBuilder()
				.withSubjectId(subjectId)
				.withEventId(eventDefinitionId)
				.build();
	}
}
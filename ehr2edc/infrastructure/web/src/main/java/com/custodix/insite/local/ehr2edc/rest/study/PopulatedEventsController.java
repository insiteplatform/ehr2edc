package com.custodix.insite.local.ehr2edc.rest.study;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.GetEvent;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/populated/{eventId}")
final class PopulatedEventsController {
	private final GetEvent getEvent;
	private final GetItemProvenance getItemProvenance;

	PopulatedEventsController(GetEvent getEvent, GetItemProvenance getItemProvenance) {
		this.getEvent = getEvent;
		this.getItemProvenance = getItemProvenance;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	GetEvent.Response getEvent(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @PathVariable EventId eventId) {
		GetEvent.Request request = GetEvent.Request.newBuilder()
				.withSubjectId(subjectId)
				.withEventId(eventId)
				.build();

		return getEvent.getEvent(request);
	}

	@GetMapping(value = "/items/{itemId}/provenance", produces = APPLICATION_JSON_VALUE)
	GetItemProvenance.Response getItemProvenance(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @PathVariable EventId eventId, @PathVariable ItemId itemId) {
		GetItemProvenance.Request request = GetItemProvenance.Request.newBuilder()
				.withSubjectId(subjectId)
				.withEventId(eventId)
				.withItemId(itemId)
				.build();
		return getItemProvenance.get(request);
	}
}
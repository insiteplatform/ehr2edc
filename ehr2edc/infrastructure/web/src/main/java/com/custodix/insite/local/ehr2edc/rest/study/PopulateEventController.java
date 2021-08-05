package com.custodix.insite.local.ehr2edc.rest.study;

import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.PopulateEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/events")
public final class PopulateEventController {

	private PopulateEvent populateEvent;

	PopulateEventController(PopulateEvent populateEvent) {
		this.populateEvent = populateEvent;
	}

	@PostMapping
	public PopulateEvent.Response populateEvent(@PathVariable("studyId") String studyId,
			@PathVariable("subjectId") String subjectId, @RequestBody PopulateEventRequestBody requestBody,
			HttpServletResponse httpResponse
	) {

		PopulateEvent.Request request = PopulateEvent.Request.newBuilder()
				.withReferenceDate(requestBody.getReferenceDate())
				.withEventDefinitionId(requestBody.getEventDefinitionId())
				.withStudyId(StudyId.of(studyId))
				.withSubjectId(SubjectId.of(subjectId))
				.build();

		final PopulateEvent.Response response = populateEvent.populate(request);
		httpResponse.setStatus(HttpServletResponse.SC_CREATED);
		return response;
	}

	static final class PopulateEventRequestBody {
		private LocalDate referenceDate;
		private EventDefinitionId eventDefinitionId;

		@JsonCreator
		private PopulateEventRequestBody(
				@JsonProperty(value = "referenceDate", required = true) LocalDate referenceDate,
				@JsonProperty(value = "eventDefinitionId", required = true) EventDefinitionId eventDefinitionId) {
			this.referenceDate = referenceDate;
			this.eventDefinitionId = eventDefinitionId;
		}

		LocalDate getReferenceDate() {
			return referenceDate;
		}

		EventDefinitionId getEventDefinitionId() {
			return eventDefinitionId;
		}
	}
}

package com.custodix.insite.local.ehr2edc.rest.subject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.GetObservationSummary;
import com.custodix.insite.local.ehr2edc.query.GetObservationSummary.Request;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/observations")
public final class SubjectObservationSummaryController {

	private final GetObservationSummary getObservationSummary;

	SubjectObservationSummaryController(GetObservationSummary getObservationSummary) {
		this.getObservationSummary = getObservationSummary;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public GetObservationSummary.Response getObservationSummary(@PathVariable StudyId studyId, @PathVariable SubjectId subjectId) {
		Request request = Request.newBuilder()
				.withSubjectId(subjectId)
				.build();
		return getObservationSummary.getSummary(request);
	}
}
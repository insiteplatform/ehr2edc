package com.custodix.insite.local.ehr2edc.rest.study;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent;
import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent.Request;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEvent;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEventHistory;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;
import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/submitted")
public final class SubmittedEventsController {
	private final SubmitReviewedEvent submitReviewedEvent;
	private final GetSubmittedEvent getSubmittedEvent;
	private final GetSubmittedEventHistory getSubmittedEventHistory;
	private final GetSubmittedItemProvenance getSubmittedItemProvenance;

	SubmittedEventsController(SubmitReviewedEvent submitReviewedEvent, GetSubmittedEvent getSubmittedEvent,
			GetSubmittedEventHistory getSubmittedEventHistory, GetSubmittedItemProvenance getSubmittedItemProvenance) {
		this.submitReviewedEvent = submitReviewedEvent;
		this.getSubmittedEvent = getSubmittedEvent;
		this.getSubmittedEventHistory = getSubmittedEventHistory;
		this.getSubmittedItemProvenance = getSubmittedItemProvenance;
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public void submitReviewedEvent(@PathVariable StudyId studyId, @PathVariable SubjectId subjectId,
			@RequestBody SubmitReviewedEventRequestBody submitReviewedEventRequestBody,
			HttpServletResponse httpResponse) {
		final Request request = Request.newBuilder()
				.withEventId(submitReviewedEventRequestBody.getPopulatedEventId())
				.withReviewedForms(submitReviewedEventRequestBody.getReviewedForms())
				.build();
		final SubmitReviewedEvent.Response response = submitReviewedEvent.submit(request);
		httpResponse.setStatus(HttpServletResponse.SC_CREATED);
		httpResponse.setHeader("Location", response.getSubmittedEventId().getId());
	}

	@GetMapping(value = "/{eventId}", produces = APPLICATION_JSON_VALUE)
	public GetSubmittedEvent.Response getSubmittedEvent(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @PathVariable SubmittedEventId eventId) {
		GetSubmittedEvent.Request request = GetSubmittedEvent.Request.newBuilder()
				.withSubmittedEventId(eventId)
				.build();
		return getSubmittedEvent.get(request);
	}

	@GetMapping(value = "/{eventId}/history", produces = APPLICATION_JSON_VALUE)
	public GetSubmittedEventHistory.Response getReviewedEvents(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @PathVariable EventId eventId) {
		GetSubmittedEventHistory.Request request = GetSubmittedEventHistory.Request.newBuilder()
				.withSubjectId(subjectId)
				.withEventId(eventId)
				.build();
		return getSubmittedEventHistory.get(request);
	}

	@GetMapping(value = "/{eventId}/items/{itemId}/provenance",
				produces = APPLICATION_JSON_VALUE)
	GetSubmittedItemProvenance.Response getSubmittedItemProvenance(@PathVariable StudyId studyId,
			@PathVariable SubjectId subjectId, @PathVariable SubmittedEventId eventId,
			@PathVariable SubmittedItemId itemId) {
		GetSubmittedItemProvenance.Request request = GetSubmittedItemProvenance.Request.newBuilder()
				.withSubjectId(subjectId)
				.withSubmittedEventId(eventId)
				.withSubmittedItemId(itemId)
				.build();
		return getSubmittedItemProvenance.get(request);
	}

	@JsonDeserialize(builder = SubmitReviewedEventRequestBody.Builder.class)
	static final class SubmitReviewedEventRequestBody {
		private EventId populatedEventId;
		private List<SubmitReviewedEvent.ReviewedForm> reviewedForms;

		private SubmitReviewedEventRequestBody(Builder builder) {
			populatedEventId = builder.populatedEventId;
			reviewedForms = builder.reviewedForms;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		EventId getPopulatedEventId() {
			return populatedEventId;
		}

		List<SubmitReviewedEvent.ReviewedForm> getReviewedForms() {
			return reviewedForms;
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private EventId populatedEventId;
			private List<SubmitReviewedEvent.ReviewedForm> reviewedForms;

			private Builder() {
			}

			public Builder withPopulatedEventId(EventId val) {
				populatedEventId = val;
				return this;
			}

			public Builder withReviewedForms(List<SubmitReviewedEvent.ReviewedForm> val) {
				reviewedForms = val;
				return this;
			}

			public SubmitReviewedEventRequestBody build() {
				return new SubmitReviewedEventRequestBody(this);
			}
		}
	}
}
package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.web.client.HttpClientErrorException;

import com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response.RaveErrorResponse;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response.ResponseConverter;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;

class SubmitReviewedEventErrorResponseHandler {

	private static final String EVENT_KEY = "StudyEventData";
	private static final String FORM_KEY = "FormData";
	private static final String ITEMGROUP_KEY = "ItemGroupData";
	private static final String ITEM_KEY = "ItemData";

	private final ResponseConverter responseConverter;
	private final ODMXPathParser xPathParser;

	SubmitReviewedEventErrorResponseHandler(ResponseConverter responseConverter) {
		this.responseConverter = responseConverter;
		this.xPathParser = new ODMXPathParser();
	}

	void convertToConstraintViolation(SubmittedEvent reviewedEvent, HttpClientErrorException hcee) {
		throw toRaveResponse().andThen(extractViolationFrom(reviewedEvent))
				.andThen(Collections::singletonList)
				.andThen(UseCaseConstraintViolationException::new)
				.apply(hcee);
	}

	RaveErrorResponse convertToRaveErrorResponse(HttpClientErrorException hcee) {
		return toRaveResponse().apply(hcee);
	}

	private Function<HttpClientErrorException, RaveErrorResponse> toRaveResponse() {
		return hcee -> {
			try {
				return responseConverter.deserialize(hcee.getResponseBodyAsString(), RaveErrorResponse.class);
			} catch (IOException e) {
				throw hcee;
			}
		};
	}

	private Function<RaveErrorResponse, UseCaseConstraintViolation> extractViolationFrom(SubmittedEvent event) {
		return response -> {
			String instanceId = findViolator(event, response.getErrorOriginLocation());
			return violationFor(instanceId, response.getErrorClientResponseMessage());
		};
	}

	private UseCaseConstraintViolation violationFor(String instanceId, String message) {
		return UseCaseConstraintViolation.constraintViolation(instanceId, message);
	}

	private String findViolator(SubmittedEvent event, String xpath) {
		Map<String, Integer> indices = xPathParser.elementIndices(xpath);
		return findFormFor(event, indices).flatMap(descendantOrFormInstanceId(indices))
				.orElse("");
	}

	private Optional<SubmittedForm> findFormFor(SubmittedEvent event, Map<String, Integer> indices) {
		if (indices.containsKey(EVENT_KEY) && indices.containsKey(FORM_KEY)) {
			return Optional.of(event.getSubmittedForms()
					.get(indices.get(FORM_KEY)));
		}
		return Optional.empty();
	}

	private Function<SubmittedForm, Optional<String>> descendantOrFormInstanceId(Map<String, Integer> indices) {
		return form -> {
			if (indices.containsKey(ITEMGROUP_KEY)) {
				return Optional.of(form.getSubmittedItemGroups()
						.get(indices.get(ITEMGROUP_KEY)))
						.map(itemOrGroupInstanceId(indices));
			}
			return Optional.of(form.getPopulatedFormId()
					.getId());
		};
	}

	private Function<SubmittedItemGroup, String> itemOrGroupInstanceId(Map<String, Integer> indices) {
		return itemGroup -> {
			if (indices.containsKey(ITEM_KEY)) {
				return itemGroup.getSubmittedItems()
						.get(indices.get(ITEM_KEY))
						.getPopulatedItemId()
						.getId();
			}
			return itemGroup.getPopulatedItemGroupId()
					.getId();
		};
	}
}

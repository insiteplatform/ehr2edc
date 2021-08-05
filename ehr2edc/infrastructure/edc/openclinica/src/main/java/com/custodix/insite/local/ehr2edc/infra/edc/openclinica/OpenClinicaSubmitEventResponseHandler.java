package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.EventSubmissionResultRecord.Type.FORM;
import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.EventSubmissionResultRecord.Type.ITEM;
import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.EventSubmissionResultRecord.Type.ITEM_GROUP;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

class OpenClinicaSubmitEventResponseHandler {
	private static final Pattern RESPONSE_PATTERN = Pattern.compile("job uuid: (.*)");

	private final OpenClinicaImportJobReader importJobReader;
	private final ErrorTranslationService errorTranslationService;

	OpenClinicaSubmitEventResponseHandler(OpenClinicaImportJobReader importJobReader,
			ErrorTranslationService errorTranslationService) {
		this.importJobReader = importJobReader;
		this.errorTranslationService = errorTranslationService;
	}

	void handleResponse(String response, AuthenticationToken authenticationToken, SubmittedEvent reviewedEvent) {
		Matcher matcher = RESPONSE_PATTERN.matcher(response);
		if (matcher.find()) {
			EventSubmissionResult result = importJobReader.readJob(matcher.group(1), authenticationToken);
			validateResult(result, reviewedEvent);
		} else {
			throwExceptionForErrorCode(response);
		}
	}

	private void validateResult(EventSubmissionResult result, SubmittedEvent reviewedEvent) {
		checkGeneralError(result);
		checkSpecificErrors(result, reviewedEvent);
	}

	private void checkGeneralError(EventSubmissionResult result) {
		result.findGeneralFailure()
				.ifPresent(f -> throwExceptionForErrorCode(f.getErrorCode()));
	}

	private void checkSpecificErrors(EventSubmissionResult result, SubmittedEvent reviewedEvent) {
		List<UseCaseConstraintViolation> violations = new ArrayList<>();
		violations.addAll(getViolations(result.getFailures(FORM), reviewedEvent, this::createFormViolation));
		violations.addAll(getViolations(result.getFailures(ITEM_GROUP), reviewedEvent, this::createItemGroupViolation));
		violations.addAll(getViolations(result.getFailures(ITEM), reviewedEvent, this::createItemViolation));
		if (!violations.isEmpty()) {
			throw new UseCaseConstraintViolationException(violations);
		}
	}

	private List<UseCaseConstraintViolation> getViolations(List<EventSubmissionResultRecord> formFailures,
			SubmittedEvent reviewedEvent,
			BiFunction<EventSubmissionResultRecord, SubmittedEvent, UseCaseConstraintViolation> createViolation) {
		return formFailures.stream()
				.map(f -> createViolation.apply(f, reviewedEvent))
				.collect(Collectors.toList());
	}

	private UseCaseConstraintViolation createFormViolation(EventSubmissionResultRecord failure, SubmittedEvent event) {
		SubmittedForm submittedForm = event.getSubmittedForm(FormDefinitionId.of(failure.getFormOID()));
		return createViolation(submittedForm.getPopulatedFormId()
				.getId(), failure.getErrorCode());
	}

	private UseCaseConstraintViolation createItemGroupViolation(EventSubmissionResultRecord failure,
			SubmittedEvent event) {
		SubmittedItemGroup submittedItemGroup = event.getSubmittedItemGroup(FormDefinitionId.of(failure.getFormOID()),
				ItemGroupDefinitionId.of(failure.getItemGroupOID()));
		return createViolation(submittedItemGroup.getPopulatedItemGroupId()
				.getId(), failure.getErrorCode());
	}

	private UseCaseConstraintViolation createItemViolation(EventSubmissionResultRecord failure, SubmittedEvent event) {
		SubmittedItem submittedItem = event.getSubmittedItem(FormDefinitionId.of(failure.getFormOID()),
				ItemGroupDefinitionId.of(failure.getItemGroupOID()), ItemDefinitionId.of(failure.getItemOID()));
		return createViolation(submittedItem.getPopulatedItemId()
				.getId(), failure.getErrorCode());
	}

	private UseCaseConstraintViolation createViolation(String field, String errorCode) {
		String message = errorTranslationService.translate(errorCode);
		return UseCaseConstraintViolation.constraintViolation(field, message);
	}

	private void throwExceptionForErrorCode(String errorCode) {
		String message = errorTranslationService.translate(errorCode);
		throw new UserException(message);
	}
}

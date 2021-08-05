package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.OpenClinicaODMSerializer;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.mapper.ClinicalDataODMMapper;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.ODM;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class OpenClinicaSubmitEvent {
	private final ClinicalDataODMMapper odmMapper;
	private final OpenClinicaODMSerializer odmSerializer;
	private final AuthenticationTokenProvider authenticationTokenProvider;
	private final OpenClinicaRestClient restClient;
	private final ErrorTranslationService errorTranslationService;
	private final OpenClinicaSubmitEventResponseHandler responseHandler;

	OpenClinicaSubmitEvent(OpenClinicaODMSerializer odmSerializer,
			AuthenticationTokenProvider authenticationTokenProvider, OpenClinicaRestClient restClient,
			ErrorTranslationService errorTranslationService, OpenClinicaSubmitEventResponseHandler responseHandler) {
		this.odmMapper = new ClinicalDataODMMapper();
		this.odmSerializer = odmSerializer;
		this.authenticationTokenProvider = authenticationTokenProvider;
		this.restClient = restClient;
		this.errorTranslationService = errorTranslationService;
		this.responseHandler = responseHandler;
	}

	String submitEvent(ExternalEDCConnection connection, SubmittedEvent reviewedEvent, Study study) {
		ODM submitODM = odmMapper.mapSubmittedEventODMFor(reviewedEvent, study, connection);
		String submitXml = odmSerializer.serialize(submitODM);
		submit(connection, submitXml, reviewedEvent);
		return submitXml;
	}

	private void submit(ExternalEDCConnection connection, String submitXml, SubmittedEvent reviewedEvent) {
		AuthenticationToken authenticationToken = authenticationTokenProvider.get(connection);
		try {
			ResponseEntity<String> response = restClient.postXmlAsMultipartData(connection.getClinicalDataURI(),
					submitXml, authenticationToken);
			responseHandler.handleResponse(response.getBody(), authenticationToken, reviewedEvent);
		} catch (HttpClientErrorException e) {
			handleException(e);
		}
	}

	private void handleException(HttpClientErrorException e) {
		String message = errorTranslationService.translate(e.getResponseBodyAsString());
		throw new UserException(message);
	}
}

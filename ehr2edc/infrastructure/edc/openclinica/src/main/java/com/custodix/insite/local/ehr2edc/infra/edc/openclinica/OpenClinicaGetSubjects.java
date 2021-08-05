package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class OpenClinicaGetSubjects {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenClinicaGetSubjects.class);

	private final AuthenticationTokenProvider authenticationTokenProvider;
	private final OpenClinicaRestClient restClient;
	private final OpenClinicaGetSubjectsResponseHandler responseHandler;

	OpenClinicaGetSubjects(AuthenticationTokenProvider authenticationTokenProvider, OpenClinicaRestClient restClient,
			OpenClinicaGetSubjectsResponseHandler responseHandler) {
		this.authenticationTokenProvider = authenticationTokenProvider;
		this.restClient = restClient;
		this.responseHandler = responseHandler;
	}

	List<EDCSubjectReference> getSubjects(ExternalEDCConnection connection) {
		try {
			return doGetSubjects(connection);
		} catch (Exception e) {
			LOGGER.error("Failed getting subjects for study '{}' from OpenClinica with URL '{}'",
					connection.getStudyId(), connection.getClinicalDataURI(), e);
			return emptyList();
		}
	}

	private List<EDCSubjectReference> doGetSubjects(ExternalEDCConnection connection) throws IOException {
		AuthenticationToken authenticationToken = authenticationTokenProvider.get(connection);
		ResponseEntity<String> response = restClient.get(connection.getClinicalDataURI(), authenticationToken);
		return responseHandler.handleResponse(response.getBody());
	}
}

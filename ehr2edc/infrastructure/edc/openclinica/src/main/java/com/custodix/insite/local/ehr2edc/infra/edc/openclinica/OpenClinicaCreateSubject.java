package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static java.lang.String.format;

import org.springframework.web.client.HttpClientErrorException;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class OpenClinicaCreateSubject {
	private final AuthenticationTokenProvider authenticationTokenProvider;
	private final OpenClinicaRestClient restClient;
	private final OpenClinicaCreateSubjectHttpErrorHandler httpErrorHandler;

	OpenClinicaCreateSubject(AuthenticationTokenProvider authenticationTokenProvider, OpenClinicaRestClient restClient,
			OpenClinicaCreateSubjectHttpErrorHandler httpErrorHandler) {
		this.authenticationTokenProvider = authenticationTokenProvider;
		this.restClient = restClient;
		this.httpErrorHandler = httpErrorHandler;
	}

	void create(ExternalEDCConnection connection, EDCSubjectReference edcSubjectReference) {
		try {
			doCreate(connection, edcSubjectReference);
		} catch (UserException userException) {
			throw userException;
		} catch (Exception e) {
			handleUnexpectedError(connection, edcSubjectReference, e);
		}
	}

	private void doCreate(ExternalEDCConnection connection, EDCSubjectReference edcSubjectReference) {
		AuthenticationToken authenticationToken = authenticationTokenProvider.get(connection);
		CreateSubjectRequest request = new CreateSubjectRequest(edcSubjectReference.getId());
		try {
			restClient.post(connection.getClinicalDataURI(), request, authenticationToken);
		} catch (HttpClientErrorException e) {
			httpErrorHandler.handle(e);
		}
	}

	private void handleUnexpectedError(ExternalEDCConnection connection, EDCSubjectReference edcSubjectReference,
			Exception e) {
		throw new SystemException(format("Failed creating subject with reference '%s' on OpenClinica with URL '%s'",
				edcSubjectReference.getId(), connection.getClinicalDataURI()), e);
	}

	private static final class CreateSubjectRequest {
		private final String subjectKey;

		CreateSubjectRequest(String subjectKey) {
			this.subjectKey = subjectKey;
		}

		public String getSubjectKey() {
			return subjectKey;
		}
	}
}

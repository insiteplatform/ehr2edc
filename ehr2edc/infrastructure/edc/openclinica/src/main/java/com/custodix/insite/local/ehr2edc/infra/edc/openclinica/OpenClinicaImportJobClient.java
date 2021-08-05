package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static java.lang.String.format;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriTemplate;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

class OpenClinicaImportJobClient {
	private static final String IN_PROGRESS_CODE = "jobInProgress";

	private final OpenClinicaRestClient restClient;
	private final UriTemplate importJobUri;

	OpenClinicaImportJobClient(OpenClinicaRestClient restClient, UriTemplate importJobUri) {
		this.restClient = restClient;
		this.importJobUri = importJobUri;
	}

	@Retryable(
			value = { JobInProgressException.class },
			maxAttempts = 30,
			backoff = @Backoff(delay = 1000, maxDelay = 10000, multiplier = 2))
	String getJobReport(String jobUUID, AuthenticationToken authenticationToken) {
		String response = get(jobUUID, authenticationToken);
		if (response.contains(IN_PROGRESS_CODE)) {
			throw new JobInProgressException();
		}
		return response;
	}

	private String get(String jobUUID, AuthenticationToken authenticationToken) {
		try {
			return restClient.get(importJobUri.expand(jobUUID), authenticationToken)
					.getBody();
		} catch (HttpClientErrorException e) {
			throw new SystemException(format("Error when reading job with uuid '%s'", jobUUID), e);
		}
	}

	private static final class JobInProgressException extends RuntimeException {
	}
}

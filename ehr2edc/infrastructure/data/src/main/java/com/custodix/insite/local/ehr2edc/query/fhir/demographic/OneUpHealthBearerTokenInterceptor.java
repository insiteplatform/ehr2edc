package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import java.io.IOException;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class OneUpHealthBearerTokenInterceptor implements IClientInterceptor {

	/*
	 Todo this token is hardcoded and for testing purposes needs to be replaced, this class is where the actual oauth flow for 1 up would be implemented
	 */
	private static final String AUTHORIZATION_TOKEN = "Bearer grizzly-token";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

	@Override
	public void interceptRequest(IHttpRequest request) {
		if (isACallToOneUpHealthApi(request) && doesNotContainAuthorizationBearerToken(request)){
			request.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_TOKEN);
		}
	}

	private boolean isACallToOneUpHealthApi(IHttpRequest request) {
		return request.getUri().contains("api.1up.health");
	}

	private boolean doesNotContainAuthorizationBearerToken(IHttpRequest request) {
		return !request.getAllHeaders().containsKey("Authorization");
	}

	@Override
	public void interceptResponse(IHttpResponse response) throws IOException {
	}
}

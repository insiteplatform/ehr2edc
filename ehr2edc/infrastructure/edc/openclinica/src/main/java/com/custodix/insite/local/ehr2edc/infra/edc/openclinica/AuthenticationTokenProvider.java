package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.net.URI;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

public class AuthenticationTokenProvider {
	private final URI uri;
	private final OpenClinicaRestClient restClient;

	AuthenticationTokenProvider(URI uri, OpenClinicaRestClient restClient) {
		this.uri = uri;
		this.restClient = restClient;
	}

	public AuthenticationToken get(ExternalEDCConnection connection) {
		RequestBody requestBody = new RequestBody(connection.getUsername(), connection.getPassword());
		try {
			return new AuthenticationToken(restClient.post(uri, requestBody));
		} catch (Exception e) {
			throw new SystemException("Could not retrieve EDC authentication token", e);
		}
	}

	private static final class RequestBody {
		private final String username;
		private final String password;

		RequestBody(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}
}

package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.net.URI;

import org.springframework.web.util.UriTemplate;

class OpenClinicaSettings {
	private final URI authenticationURI;
	private final UriTemplate importJobURI;

	OpenClinicaSettings(URI authenticationURI, UriTemplate importJobURI) {
		this.authenticationURI = authenticationURI;
		this.importJobURI = importJobURI;
	}

	URI getAuthenticationURI() {
		return authenticationURI;
	}

	UriTemplate getImportJobURI() {
		return importJobURI;
	}
}
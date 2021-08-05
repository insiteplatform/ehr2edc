package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import static java.util.Arrays.asList;

import java.net.URI;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

class RaveRestClient {
	private static final String UTF8_BOM = "\u00EF\u00BB\u00BF";

	String get(URI uri, String username, String password) {
		RestTemplate template = getInstance(username, password);
		return removeByteOrderMarker(template.getForObject(uri, String.class));
	}

	private String removeByteOrderMarker(String response) {
		if (response == null)
			return null;

		if (response.startsWith(UTF8_BOM)) {
			return response.substring(3);
		}
		return response;
	}

	void postXml(URI uri, String xml, String username, String password) {
		RestTemplate template = getInstance(username, password);
		template.postForEntity(uri, toXMLRequest(xml), String.class);
	}

	private HttpEntity<String> toXMLRequest(String body) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_XML);

		return new HttpEntity<>(body, httpHeaders);
	}

	private RestTemplate getInstance(String username, String password) {
		return addBasicAuth(createRestTemplate(), username, password);
	}

	private RestTemplate createRestTemplate() {
		return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
	}

	private RestTemplate addBasicAuth(RestTemplate restTemplate, String username, String password) {
		if (Objects.nonNull(username) && Objects.nonNull(password)) {
			restTemplate.setInterceptors(
					asList(new LoggingRequestInterceptor(), new BasicAuthenticationInterceptor(username, password)));
		}
		return restTemplate;
	}
}

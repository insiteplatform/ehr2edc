package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static java.util.Collections.singletonList;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

class OpenClinicaRestClient {
	ResponseEntity<String> get(URI uri, AuthenticationToken authenticationToken) {
		RestTemplate template = createRestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken.getValue());
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(httpHeaders);
		return template.exchange(uri, HttpMethod.GET, httpEntity, String.class);
	}

	ResponseEntity<String> post(URI uri, Object json, AuthenticationToken authenticationToken) {
		RestTemplate template = createRestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken.getValue());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> httpEntity = new HttpEntity<>(json, httpHeaders);
		return template.exchange(uri, HttpMethod.POST, httpEntity, String.class);
	}

	String post(URI uri, Object body) {
		RestTemplate template = createRestTemplate();
		return template.exchange(uri, HttpMethod.POST, toJSONRequest(body), String.class)
				.getBody();
	}

	ResponseEntity<String> postXmlAsMultipartData(URI uri, String xml, AuthenticationToken authenticationToken) {
		RestTemplate template = createRestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setBearerAuth(authenticationToken.getValue());
		MultiValueMap<String, Object> body = createMultipartBody(xml);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, httpHeaders);
		return template.exchange(uri, HttpMethod.POST, httpEntity, String.class);
	}

	private MultiValueMap<String, Object> createMultipartBody(String xml) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		ByteArrayResource contentsAsResource = new ByteArrayResource(xml.getBytes(StandardCharsets.UTF_8)) {
			@Override
			public String getFilename() {
				return "odm.xml";
			}
		};
		body.add("file", contentsAsResource);
		return body;
	}

	private HttpEntity<Object> toJSONRequest(Object body) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(body, httpHeaders);
	}

	private RestTemplate createRestTemplate() {
		RestTemplate template = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		template.setInterceptors(singletonList(new LoggingRequestInterceptor()));
		return template;
	}
}

package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		traceRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		traceResponse(response);
		return response;
	}

	private void traceRequest(HttpRequest request, byte[] body) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("===========================request begin=============================================");
			LOGGER.trace("URI         : {}", request.getURI());
			LOGGER.trace("Method      : {}", request.getMethod());
			LOGGER.trace("Headers     : {}", request.getHeaders());
			LOGGER.trace("Request body: {}", new String(body, StandardCharsets.UTF_8));
			LOGGER.trace("===========================request end===============================================");
		}
	}

	private void traceResponse(ClientHttpResponse response) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("===========================response begin============================================");
			LOGGER.trace("Status code  : {}", response.getStatusCode());
			LOGGER.trace("Status text  : {}", response.getStatusText());
			LOGGER.trace("Headers      : {}", response.getHeaders());
			LOGGER.trace("Response body: {}", getBody(response));
			LOGGER.trace("===========================response end==============================================");
		}
	}

	private String getBody(ClientHttpResponse response) throws IOException {
		StringBuilder inputStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));

		removeUTF8ByteOrderMarker(bufferedReader);

		String line = bufferedReader.readLine();
		while (line != null) {
			inputStringBuilder.append(line);
			inputStringBuilder.append('\n');
			line = bufferedReader.readLine();
		}
		return inputStringBuilder.toString();
	}

	private void removeUTF8ByteOrderMarker(BufferedReader bufferedReader) throws IOException {
		bufferedReader.mark(4);
		if ('\ufeff' != bufferedReader.read())
			bufferedReader.reset();
	}

}
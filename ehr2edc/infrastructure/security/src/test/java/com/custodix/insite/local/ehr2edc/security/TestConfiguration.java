package com.custodix.insite.local.ehr2edc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;

@Configuration
public class TestConfiguration {

	@Bean
	public AuthorizedRequest authorizedExample() {
		return new AuthorizedUseCase();
	}

	@Bean
	public PublicRequest publicExample() {
		return new PublicUseCase();
	}

	@Command
	static class AuthorizedUseCase implements AuthorizedRequest {

		@Override
		public void doAuthenticatedRequest(Request request) {
		}

		@Override
		public void doRequest(Request request) {
		}

		@Override
		public void doDRMRequest() {
		}
	}

	@Query
	static class PublicUseCase implements PublicRequest {

		@Override
		public void doRequest(Request request) {
		}

		@Override
		public void doDisallowedRequest(Request request) {
		}
	}
}

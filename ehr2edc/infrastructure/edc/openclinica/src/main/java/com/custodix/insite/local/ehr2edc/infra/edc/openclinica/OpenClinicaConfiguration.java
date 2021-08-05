package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.net.URI;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.util.UriTemplate;

import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.OpenClinicaODMSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableRetry
public class OpenClinicaConfiguration {
	@Bean
	OpenClinicaEDCStudyGateway openClinicaEDCStudyGateway(OpenClinicaGetSubjects getSubjects,
			OpenClinicaCreateSubject createSubject, OpenClinicaIsRegisteredSubject isRegisteredSubject,
			OpenClinicaSubmitEvent openClinicaSubmitEvent) {
		return new OpenClinicaEDCStudyGateway(getSubjects, createSubject, isRegisteredSubject, openClinicaSubmitEvent);
	}

	@Bean
	OpenClinicaGetSubjects openClinicaGetSubjects(AuthenticationTokenProvider authenticationTokenProvider,
			OpenClinicaRestClient openClinicaRestClient, OpenClinicaGetSubjectsResponseHandler responseHandler) {
		return new OpenClinicaGetSubjects(authenticationTokenProvider, openClinicaRestClient, responseHandler);
	}

	@Bean
	OpenClinicaCreateSubject openClinicaCreateSubject(AuthenticationTokenProvider authenticationTokenProvider,
			OpenClinicaRestClient restClient, OpenClinicaCreateSubjectHttpErrorHandler httpErrorHandler) {
		return new OpenClinicaCreateSubject(authenticationTokenProvider, restClient, httpErrorHandler);
	}

	@Bean
	OpenClinicaSubmitEvent openClinicaSubmitEvent(OpenClinicaODMSerializer odmSerializer,
			AuthenticationTokenProvider authenticationTokenProvider, OpenClinicaRestClient openClinicaRestClient,
			ErrorTranslationService errorTranslationService, OpenClinicaSubmitEventResponseHandler responseHandler) {
		return new OpenClinicaSubmitEvent(odmSerializer, authenticationTokenProvider, openClinicaRestClient,
				errorTranslationService, responseHandler);
	}

	@Bean
	OpenClinicaIsRegisteredSubject openClinicaIsRegisteredSubject(OpenClinicaGetSubjects openClinicaGetSubjects) {
		return new OpenClinicaIsRegisteredSubject(openClinicaGetSubjects);
	}

	@Bean
	OpenClinicaGetSubjectsResponseHandler openClinicaGetSubjectsResponseHandler() {
		return new OpenClinicaGetSubjectsResponseHandler(createJsonObjectMapper());
	}

	@Bean
	OpenClinicaCreateSubjectHttpErrorHandler openClinicaCreateSubjectHttpErrorHandler(
			ErrorTranslationService errorTranslationService) {
		return new OpenClinicaCreateSubjectHttpErrorHandler(createJsonObjectMapper(), errorTranslationService);
	}

	@Bean
	OpenClinicaSubmitEventResponseHandler openClinicaSubmitEventResponseHandler(
			OpenClinicaImportJobReader importJobReader, ErrorTranslationService errorTranslationService) {
		return new OpenClinicaSubmitEventResponseHandler(importJobReader, errorTranslationService);
	}

	@Bean
	OpenClinicaImportJobReader openClinicaImportJobReader(OpenClinicaImportJobClient openClinicaImportJobClient) {
		return new OpenClinicaImportJobReader(openClinicaImportJobClient);
	}

	@Bean
	OpenClinicaImportJobClient openClinicaImportJobClient(OpenClinicaRestClient restClient,
			OpenClinicaSettings openClinicaSettings) {
		return new OpenClinicaImportJobClient(restClient, openClinicaSettings.getImportJobURI());
	}

	@Bean
	AuthenticationTokenProvider authenticationTokenProvider(OpenClinicaRestClient openClinicaRestClient,
			OpenClinicaSettings openClinicaSettings) {
		return new AuthenticationTokenProvider(openClinicaSettings.getAuthenticationURI(), openClinicaRestClient);
	}

	@Bean
	OpenClinicaRestClient openClinicaRestClient() {
		return new OpenClinicaRestClient();
	}

	@Bean
	OpenClinicaODMSerializer openClinicaODMSerializer() throws JAXBException {
		return new OpenClinicaODMSerializer();
	}

	@Bean
	ErrorTranslationService errorTranslationService(
			@Qualifier("openClinicaErrorMessageSource") MessageSource messageSource) {
		return new ErrorTranslationService(messageSource);
	}

	@Bean
	ReloadableResourceBundleMessageSource openClinicaErrorMessageSource() {
		final ReloadableResourceBundleMessageSource errors = new ReloadableResourceBundleMessageSource();
		errors.setBasename("classpath:com/custodix/insite/local/ehr2edc/infra/edc/openclinica/errors");
		return errors;
	}

	@Bean
	OpenClinicaSettings openClinicaSettings(
			@Value("${edc.openclinica.authentication.uri:https://sandbox-sales.build.openclinica.io/user-service/api/oauth/token}") String authenticationURI,
			@Value("${edc.openclinica.import.job.uri:https://sandbox-sales.openclinica.io/OpenClinica/pages/auth/api/jobs/{job}/downloadFile}") String importJobURI) {
		return new OpenClinicaSettings(URI.create(authenticationURI), new UriTemplate(importJobURI));
	}

	private ObjectMapper createJsonObjectMapper() {
		return new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}

package com.custodix.insite.local.ehr2edc.security.oidc;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.custodix.insite.local.ehr2edc.security.AuthorityType;
import com.custodix.insite.local.ehr2edc.security.EHR2EDCAuthenticationProvider;
import com.custodix.insite.local.ehr2edc.security.EHR2EDCSecurityConfigProperties;

@ConditionalOnProperty("ehr2edc.oidc.enabled")
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSecurity
@EnableConfigurationProperties(EHR2EDCSecurityConfigProperties.class)
class OIDCWebSecurity extends WebSecurityConfigurerAdapter {
	private static final String UNAUTHENTICATED_ITEM_QUERY_MAPPING = "/ehr2edc/studies/{studyId:.+}/item-query-mappings/**";
	private static final String UNAUTHENTICATED_CREATE_STUDY = "/ehr2edc/studies";
	private static final String UNAUTHENTICATED_UPDATE_STUDY = "/ehr2edc/studies/{studyId:.+}/metadata";
	private static final String UNAUTHENTICATED_UPDATE_STUDY_EDC = "/ehr2edc/edc/connection";

	private static final String LOCALHOST = "hasIpAddress('127.0.0.1') or hasIpAddress('::1')";
	private static final String EHR2EDC_ADMINISTRATION =
			"hasRole('ROLE_" + AuthorityType.EHR2EDC_ADMINISTRATION.name() + "')";
	private static final String EHR2EDC_ADMINISTRATION_FROM_LOCALHOST =
			EHR2EDC_ADMINISTRATION + " and (" + LOCALHOST + ")";
	private static final String EHR2EDC_DATAWAREHOUSE = "/ehr2edc/datawarehouse";

	private final EHR2EDCOpenIDConnectFilter ehr2EDCOpenIDConnectFilter;
	private final EHR2EDCOpenIDConnectProperties ehr2EDCOpenIDConnectProperties;
	private final EHR2EDCAuthenticationProvider ehr2EDCAuthenticationProvider;

	OIDCWebSecurity(EHR2EDCSecurityConfigProperties ehr2EDCSecurityConfigProperties,
			EHR2EDCOpenIDConnectFilter ehr2EDCOpenIDConnectFilter,
			EHR2EDCOpenIDConnectProperties ehr2EDCOpenIDConnectProperties) {
		this.ehr2EDCAuthenticationProvider = new EHR2EDCAuthenticationProvider(ehr2EDCSecurityConfigProperties);
		this.ehr2EDCOpenIDConnectFilter = ehr2EDCOpenIDConnectFilter;
		this.ehr2EDCOpenIDConnectProperties = ehr2EDCOpenIDConnectProperties;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
				.addFilterAfter(ehr2EDCOpenIDConnectFilter, OAuth2ClientContextFilter.class)
				.httpBasic()
				.authenticationEntryPoint(
						new LoginUrlAuthenticationEntryPoint(ehr2EDCOpenIDConnectProperties.getLoginUri()))
				.and()
				.authorizeRequests()

				.antMatchers(EHR2EDC_DATAWAREHOUSE)
				.access(EHR2EDC_ADMINISTRATION_FROM_LOCALHOST)

				.antMatchers(UNAUTHENTICATED_ITEM_QUERY_MAPPING)
				.access(LOCALHOST)

				.antMatchers(HttpMethod.POST, UNAUTHENTICATED_CREATE_STUDY)
				.access(LOCALHOST)

				.antMatchers(HttpMethod.PUT, UNAUTHENTICATED_UPDATE_STUDY)
				.access(LOCALHOST)

				.antMatchers(HttpMethod.POST, UNAUTHENTICATED_UPDATE_STUDY_EDC)
				.access(LOCALHOST)

				.requestMatchers(EndpointRequest.toAnyEndpoint())
				.permitAll()

				.antMatchers("/**")
				.authenticated()
				.and()
				.csrf()
				.disable()
				.logout()
				.logoutSuccessUrl(ehr2EDCOpenIDConnectProperties.getLogoutUri());
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(ehr2EDCAuthenticationProvider);
	}
}

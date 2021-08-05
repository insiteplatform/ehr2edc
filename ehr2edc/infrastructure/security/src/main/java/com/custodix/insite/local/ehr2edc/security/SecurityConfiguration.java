package com.custodix.insite.local.ehr2edc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.custodix.insite.local.ehr2edc.query.security.IsAny;
import com.custodix.insite.local.ehr2edc.query.security.IsAssignedInvestigator;
import com.custodix.insite.local.ehr2edc.query.security.IsAuthenticated;
import com.custodix.insite.local.ehr2edc.query.security.IsDRM;

@Configuration
@EnableAspectJAutoProxy
public class SecurityConfiguration {

	@Bean
	SecurityGuard reflectionSecurityGuard(IsAny isAny,
			IsAuthenticated isAuthenticated,
			IsAssignedInvestigator isAssignedInvestigator,
			IsDRM isDRM){
		return new ReflectionSecurityGuard(isAny, isAuthenticated, isAssignedInvestigator, isDRM);
	}

	@Bean
	AuthorizationController authorizationController(SecurityGuard securityGuard){
		return new AuthorizationController(securityGuard);
	}
}

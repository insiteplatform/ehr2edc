package com.custodix.insite.local.ehr2edc.infra.edc.main;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.OpenClinicaConfiguration;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.RaveConfiguration;

@Import({ OpenClinicaConfiguration.class, RaveConfiguration.class })
@Configuration
public class EDCConfiguration {
	@Primary
	@Bean
	EDCStudyGatewayDelegator edcStudyGatewayDelegator(StudyConnectionRepository studyConnectionRepository,
			SpecificEDCStudyGatewayProvider specificEDCStudyGatewayProvider) {
		return new EDCStudyGatewayDelegator(studyConnectionRepository, specificEDCStudyGatewayProvider);
	}

	@Bean
	SpecificEDCStudyGatewayProvider specificEDCStudyGatewayProvider(Set<SpecificEDCStudyGateway> gateways) {
		return new SpecificEDCStudyGatewayProvider(gateways);
	}
}

package com.custodix.workbench.local.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.Time;

@Configuration
public class TimeConfiguration {

	@Bean
	Time time() {
		Time time = new TimeService();
		DomainTime.setTime(time);
		return time;
	}

}

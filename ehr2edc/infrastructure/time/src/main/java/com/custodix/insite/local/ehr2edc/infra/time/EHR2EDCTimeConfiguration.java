package com.custodix.insite.local.ehr2edc.infra.time;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.DomainTime;
import com.custodix.insite.local.ehr2edc.domain.service.Time;

@Configuration
public class EHR2EDCTimeConfiguration {
	@Bean
	Time ehr2edcTime() {
		Time time = new TimeService();
		DomainTime.setTime(time);
		return time;
	}

	@Bean
	Clock systemClock() {
		return Clock.systemDefaultZone();
	}
}

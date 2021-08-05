package com.custodix.insite.mongodb.export.patient.infrastructure.scheduling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.mongodb.export.patient.domain.model.DomainTime;
import com.custodix.insite.mongodb.export.patient.domain.service.Time;
import com.custodix.insite.mongodb.export.patient.infrastructure.time.TimeService;

@Configuration
public class MongoMigratorTimeConfiguration {
	@Bean
	Time mongoMigratorTime() {
		Time time = new TimeService();
		DomainTime.setTime(time);
		return time;
	}
}

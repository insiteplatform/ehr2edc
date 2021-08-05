package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SynchronizationTestConfiguration {

	@Bean
	public FastCommand command() {
		return new FastCommand();
	}

	@Bean
	public LongRunningCommand longRunningCommand() {
		return new LongRunningCommand();
	}
}

package com.custodix.workbench.local.scheduling;

import com.custodix.insite.local.user.application.api.UpdatePasswordsStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class SchedulingConfiguration {
	@ConditionalOnProperty(value = "user.password.expiration.enabled",
						   havingValue = "true")
	@Bean
	UpdatePasswordsStatusScheduler updatePasswordsStatusScheduler(
			UpdatePasswordsStatus updatePasswordsStatus) {
		return new UpdatePasswordsStatusScheduler(updatePasswordsStatus);
	}

	@Bean
	ThreadPoolTaskScheduler localWorkbenchTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setErrorHandler(scheduledTaskErrorHandler());
		scheduler.setThreadNamePrefix("localWorkbenchTaskScheduler-");
		return scheduler;
	}

	@Bean
	ScheduledTaskErrorHandler scheduledTaskErrorHandler() {
		return new ScheduledTaskErrorHandler();
	}
}

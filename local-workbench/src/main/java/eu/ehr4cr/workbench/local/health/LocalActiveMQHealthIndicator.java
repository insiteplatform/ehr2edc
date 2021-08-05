package eu.ehr4cr.workbench.local.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import eu.ehr4cr.workbench.local.service.impl.LocalActiveMQMonitor;

public class LocalActiveMQHealthIndicator implements HealthIndicator {
	private final LocalActiveMQMonitor localActiveMQMonitor;

	public LocalActiveMQHealthIndicator(LocalActiveMQMonitor localActiveMQMonitor) {
		this.localActiveMQMonitor = localActiveMQMonitor;
	}

	@Override
	public Health health() {
		long dlqMessageCount = localActiveMQMonitor.getDLQMessageCount();
		return createHealthBuilder(dlqMessageCount).withDetail("dlqMessageCount", dlqMessageCount)
				.build();
	}

	private Health.Builder createHealthBuilder(long dlqMessageCount) {
		if (dlqMessageCount == 0) {
			return Health.up();
		} else {
			return Health.down();
		}
	}
}

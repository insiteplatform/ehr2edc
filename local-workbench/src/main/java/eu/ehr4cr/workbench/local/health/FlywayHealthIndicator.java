package eu.ehr4cr.workbench.local.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.usecases.checkflywayhealth.CheckFlywayHealth;
import eu.ehr4cr.workbench.local.usecases.checkflywayhealth.CheckFlywayHealth.Response;

@Component
public class FlywayHealthIndicator implements HealthIndicator {

	private final CheckFlywayHealth checkFlywayHealth;

	@Autowired
	public FlywayHealthIndicator(CheckFlywayHealth checkFlywayHealth) {
		this.checkFlywayHealth = checkFlywayHealth;
	}

	@Override
	public Health health() {
		Response health = checkFlywayHealth.check();
		return Health.status(getHealthStatus(health))
				.withDetail("info", health)
				.build();
	}

	private Status getHealthStatus(Response health) {
		if (health.isHealthy()) {
			return Status.UP;
		} else {
			return Status.DOWN;
		}
	}
}
package eu.ehr4cr.workbench.local.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealth;
import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealth.Response;

@Component
public class GroupAuthorityHealthIndicator implements HealthIndicator {

	private final CheckGroupAuthorityHealth checkGroupAuthorityHealth;

	@Autowired
	public GroupAuthorityHealthIndicator(CheckGroupAuthorityHealth checkGroupAuthorityHealth) {
		this.checkGroupAuthorityHealth = checkGroupAuthorityHealth;
	}

	@Override
	public Health health() {
		Response response = checkGroupAuthorityHealth.checkHealth();
		return mapResponse(response);
	}

	private Health mapResponse(Response response) {
		if (response.isHealthy()) {
			return Health.up()
					.withDetail("info", response)
					.build();
		} else {
			return Health.down()
					.withDetail("info", response)
					.build();
		}
	}
}
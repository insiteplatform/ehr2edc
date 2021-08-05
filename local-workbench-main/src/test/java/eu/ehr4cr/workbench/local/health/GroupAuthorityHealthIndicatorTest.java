package eu.ehr4cr.workbench.local.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.context.junit4.SpringRunner;

import com.custodix.insite.local.LocalWorkbenchTest;

@RunWith(SpringRunner.class)
@LocalWorkbenchTest
public class GroupAuthorityHealthIndicatorTest {

	@Autowired
	private GroupAuthorityHealthIndicator groupAuthorityHealthIndicator;

	@Test
	public void postgresqlFlywayMigrationsAreHealthy() {
		Health groupAuthorityHealth = groupAuthorityHealthIndicator.health();

		assertThat(groupAuthorityHealth.getStatus()).isEqualTo(Status.UP);
		assertThat(groupAuthorityHealth.getDetails()).containsOnlyKeys("info");
	}
}
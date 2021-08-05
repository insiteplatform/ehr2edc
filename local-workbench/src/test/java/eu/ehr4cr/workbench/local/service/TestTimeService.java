package eu.ehr4cr.workbench.local.service;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestTimeService implements Time {
	private static final Date NOW = new Date();
	private Date now = NOW;

	@Override
	public Date now() {
		return now;
	}

	public void reset() {
		now = NOW;
	}

	public void timeTravelTo(Date date) {
		now = date;
	}

	public void timeTravelToThePast(int value, TimeUnit unit) {
		timeTravelToThePast(Duration.ofMillis(unit.toMillis(value)));
	}

	public void timeTravelToThePast(Duration duration) {
		now = new Date(NOW.getTime() - duration.toMillis());
	}

	public Date tomorrow() {
		return new Date(now().getTime() + TimeUnit.DAYS.toMillis(1));
	}

	public Date yesterday() {
		return new Date(now().getTime() - TimeUnit.DAYS.toMillis(1));
	}
}

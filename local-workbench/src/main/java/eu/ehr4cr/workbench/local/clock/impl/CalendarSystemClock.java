package eu.ehr4cr.workbench.local.clock.impl;

import java.util.Calendar;
import java.util.Date;

import eu.ehr4cr.workbench.local.clock.SystemClock;

/**
 * Implementation of {@link SystemClock} based on the actual time.
 */
public class CalendarSystemClock implements SystemClock {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getDate() {
		return Calendar.getInstance()
				.getTime();
	}

}

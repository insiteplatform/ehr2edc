package eu.ehr4cr.workbench.local.clock;

import java.util.Date;

/**
 * Notion of a system clock that can be used to retrieve the date of the system.
 * As such, we can provided different implementations based on our needs: based
 * on real world time, on a set value for test purposes, ...
 *
 */
public interface SystemClock {

	/**
	 * 
	 * @return the date, according to the system clock.
	 */
	Date getDate();
}
package eu.ehr4cr.workbench.local.clock.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import eu.ehr4cr.workbench.local.clock.SystemClock;

/**
 * Implementation of {@link SystemClock} based on a set frozen time value.
 */
public class FrozenSystemClock implements SystemClock {

	private Date frozenDate;

	/**
	 * @param date
	 *            the date to set this system clock to.
	 */
	@Required
	public void setDate(Date date) {
		this.frozenDate = new Date(date.getTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getDate() {
		return new Date(this.frozenDate.getTime());
	}

}

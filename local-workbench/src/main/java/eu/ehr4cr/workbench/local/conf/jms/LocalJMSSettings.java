package eu.ehr4cr.workbench.local.conf.jms;

public interface LocalJMSSettings {
	/**
	 * @return Appropriate connection pool size depends on the amount of listeners (INO-1534)
	 */
	int getPoolSize();

	long getReceiveTimeoutMillis();

	boolean isPersistent();

	boolean isJmxEnabled();

	String getDataDirectory();
}

package eu.ehr4cr.workbench.local.conf.db;

public interface DataSourceSettings {
	String getId();

	String getUrl();

	String getUser();

	String getPassword();

	String getDriverClass();

	int getPoolSize();
}

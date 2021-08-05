package eu.ehr4cr.workbench.local.conf.db;

public interface HibernateSettings {
	String getDialect();

	String getHbm2Ddl();
}

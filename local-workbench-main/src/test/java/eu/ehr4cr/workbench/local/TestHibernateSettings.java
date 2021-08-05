package eu.ehr4cr.workbench.local;

import eu.ehr4cr.workbench.local.conf.db.HibernateSettings;

public class TestHibernateSettings implements HibernateSettings {
	@Override
	public String getDialect() {
		return "org.hibernate.dialect.H2Dialect";
	}

	@Override
	public String getHbm2Ddl() {
		return "update";
	}
}

package eu.ehr4cr.workbench.local;

import eu.ehr4cr.workbench.local.conf.db.DataSourceSettings;

public class TestDataSourceSettings implements DataSourceSettings {
	@Override
	public String getId() {
		return "lwb-datasource-test";
	}

	@Override
	public String getUrl() {
		return "jdbc:h2:mem:test;MVCC=true;INIT=CREATE SCHEMA IF NOT EXISTS public\\;SET SCHEMA public\\;runscript "
				+ "from 'classpath:/db/migration/embedded/updated_groups_and_authorities.sql'\\;SET DB_CLOSE_DELAY -1";
	}

	@Override
	public String getUser() {
		return "sa";
	}

	@Override
	public String getPassword() {
		return "sa";
	}

	@Override
	public String getDriverClass() {
		return "org.h2.Driver";
	}

	@Override
	public int getPoolSize() {
		return 20;
	}
}

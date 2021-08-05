package eu.ehr4cr.workbench.local.conf.db;

class EmbeddedDataSourceSettings implements DataSourceSettings {
	private static final int POOL_SIZE = 20;

	@Override
	public String getId() {
		return "lwb-datasource-embedded";
	}

	@Override
	public String getUrl() {
		return "jdbc:h2:mem:embedded;MVCC=true;INIT=CREATE SCHEMA IF NOT EXISTS public\\;" + "SET SCHEMA public\\;"
				+ "runscript from 'classpath:/db/migration/embedded/updated_groups_and_authorities.sql'\\;"
				+ "SET DB_CLOSE_DELAY -1";
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
		return POOL_SIZE;
	}
}

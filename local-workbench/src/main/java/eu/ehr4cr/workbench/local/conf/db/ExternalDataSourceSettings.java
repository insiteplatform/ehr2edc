package eu.ehr4cr.workbench.local.conf.db;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "database")
class ExternalDataSourceSettings implements DataSourceSettings {
	@NotBlank
	private String url;
	@NotBlank
	private String userName;
	@NotBlank
	private String password;
	@NotBlank
	private String driverClass = "org.postgresql.Driver";
	@Min(1)
	private int poolSize = 20;

	@Override
	public String getId() {
		return "lwb-datasource-external";
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUser() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getDriverClass() {
		return driverClass;
	}

	@Override
	public int getPoolSize() {
		return poolSize;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
}

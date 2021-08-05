package eu.ehr4cr.workbench.local.conf.jms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jms.local")
class LocalJMSSettingsProperties implements LocalJMSSettings {
	@Min(1)
	private int poolSize = 8;
	private long receiveTimeoutMillis = 2000;
	private boolean persistent = true;
	private boolean jmxEnabled = true;
	@NotBlank
	private String dataDirectory = "activemq-data";

	@Override
	public int getPoolSize() {
		return poolSize;
	}

	@Override
	public long getReceiveTimeoutMillis() {
		return receiveTimeoutMillis;
	}

	@Override
	public boolean isPersistent() {
		return persistent;
	}

	@Override
	public boolean isJmxEnabled() {
		return jmxEnabled;
	}

	@Override
	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setReceiveTimeoutMillis(long receiveTimeoutMillis) {
		this.receiveTimeoutMillis = receiveTimeoutMillis;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public void setJmxEnabled(boolean jmxEnabled) {
		this.jmxEnabled = jmxEnabled;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
}

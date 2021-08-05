package eu.ehr4cr.workbench.local.conf.jms;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jms")
public class RemoteJMSSettingsProperties {
	@NotNull
	private URI uri;
	@NotNull
	private String username;
	@NotNull
	private String password;
	private Integer expiryTimeout;
	private Integer idleTimeout;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getExpiryTimeout() {
		return expiryTimeout;
	}

	public void setExpiryTimeout(Integer expiryTimeout) {
		this.expiryTimeout = expiryTimeout;
	}

	public Integer getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(Integer idleTimeout) {
		this.idleTimeout = idleTimeout;
	}
}

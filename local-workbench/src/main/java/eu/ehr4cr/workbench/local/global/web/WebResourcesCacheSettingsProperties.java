package eu.ehr4cr.workbench.local.global.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "web.resources.cache")
class WebResourcesCacheSettingsProperties implements WebResourcesCacheSettings {
	private MaxAge maxage;

	@Override
	public MaxAge getMaxage() {
		return maxage;
	}

	public void setMaxage(MaxAge maxage) {
		this.maxage = maxage;
	}
}

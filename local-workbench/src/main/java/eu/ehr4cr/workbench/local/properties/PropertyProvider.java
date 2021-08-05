package eu.ehr4cr.workbench.local.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyProvider {

	@Value("${global.settings.groupId}")
	private String groupId;
	@Value("${support.baseUrl:http://test.domain.com}")
	private String baseUrl;

	public String getGroupId() {
		return groupId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

}

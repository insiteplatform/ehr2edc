package eu.ehr4cr.workbench.local.properties;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import eu.ehr4cr.workbench.local.WebRoutes;

@Validated
@ConfigurationProperties("support")
public class SupportSettingsProperties implements SupportSettings {
	@NotBlank
	private String mail;
	@NotBlank
	private String installId;
	@NotBlank
	private String baseUrl;

	@Override
	public String getMailAddress() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String getInstallId() {
		return installId;
	}

	public void setInstallId(String installId) {
		this.installId = installId;
	}

	@Override
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getFeasibilityApprovalUrl() {
		return getBaseUrl() + WebRoutes.trialDesign;
	}
}

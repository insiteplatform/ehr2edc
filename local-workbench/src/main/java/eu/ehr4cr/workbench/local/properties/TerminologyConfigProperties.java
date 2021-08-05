package eu.ehr4cr.workbench.local.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "terminology")
public class TerminologyConfigProperties implements TerminologyConfig {

    private String coverageBinDefinition = "default";
    @NotBlank
    private String siteId;
    @NotBlank
    private String applicationBaseUrl;

    public String getCoverageBinDefinition() {
        return coverageBinDefinition;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getApplicationBaseUrl() {
        return applicationBaseUrl;
    }

    public void setApplicationBaseUrl(final String applicationBaseUrl) {
        this.applicationBaseUrl = applicationBaseUrl;
    }

    public void setCoverageBinDefinition(final String coverageBinDefinition) {
        this.coverageBinDefinition = coverageBinDefinition;
    }

    public void setSiteId(final String siteId) {
        this.siteId = siteId;
    }
}

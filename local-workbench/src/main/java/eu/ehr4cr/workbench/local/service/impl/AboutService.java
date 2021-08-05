package eu.ehr4cr.workbench.local.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AboutService {

	@Value("${lwb.version}")
	private String lwbVersion;

	public String getLwbVersion() {
		return lwbVersion;
	}
}

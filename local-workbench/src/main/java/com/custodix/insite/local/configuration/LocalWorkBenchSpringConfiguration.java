package com.custodix.insite.local.configuration;

import eu.ehr4cr.workbench.local.utils.ExtraConfigProps;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@Import({ ExtraConfigProps.class,
		  AsyncConfiguration.class })
@ComponentScan(basePackages = { "eu.ehr4cr.workbench.local", "com.custodix.insite.local" },
			   excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
														pattern = "com.custodix.insite.local.ehr2edc.*") })
public class LocalWorkBenchSpringConfiguration {
}

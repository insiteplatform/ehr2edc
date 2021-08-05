package com.custodix.insite.local.ehr2edc.query.mongo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { MongoQueryDataScanningMarker.class })
public class MongoQueryDataConfiguration {
}

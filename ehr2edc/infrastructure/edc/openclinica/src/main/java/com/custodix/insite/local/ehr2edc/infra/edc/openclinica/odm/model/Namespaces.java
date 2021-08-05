package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

final class Namespaces {
	private Namespaces() {
		throw new IllegalStateException("Utility class");
	}

	static final String DEFAULT = "http://www.cdisc.org/ns/odm/v1.3";
	static final String OPEN_CLINICA = "http://www.openclinica.org/ns/odm_ext_v130/v3.1";
}

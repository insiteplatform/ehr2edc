@javax.xml.bind.annotation.XmlSchema(
		xmlns = {
				@XmlNs(prefix = "", namespaceURI = DEFAULT),
				@XmlNs(prefix = "OpenClinica", namespaceURI = OPEN_CLINICA)
		},
		namespace = DEFAULT,
		elementFormDefault = XmlNsForm.QUALIFIED)
package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.Namespaces.DEFAULT;
import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.Namespaces.OPEN_CLINICA;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
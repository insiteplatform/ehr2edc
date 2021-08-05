package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;

import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.ODM;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class OpenClinicaODMSerializer {
	private static final Logger LOGGER = getLogger(OpenClinicaODMSerializer.class);

	private final JAXBContext jaxbContext;

	public OpenClinicaODMSerializer() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(ODM.class);
	}

	public String serialize(ODM odm) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			marshaller.marshal(odm, sw);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generated odm xml: {}", sw.toString());
			}
			return sw.toString();
		} catch (JAXBException e) {
			throw new SystemException("Unable to serialize ODM.", e);
		}
	}
}

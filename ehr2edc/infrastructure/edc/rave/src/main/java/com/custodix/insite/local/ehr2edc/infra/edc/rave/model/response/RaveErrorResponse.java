package com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.NONE)
public class RaveErrorResponse {
	@XmlAttribute(name = "ErrorOriginLocation")
	private String errorOriginLocation;
	@XmlAttribute(name = "ErrorClientResponseMessage")
	private String errorClientResponseMessage;
	@XmlAttribute(name = "ReasonCode")
	private String reasonCode;

	public String getErrorOriginLocation() {
		return errorOriginLocation;
	}

	public String getErrorClientResponseMessage() {
		return errorClientResponseMessage;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public enum KnownRaveReasons {
		SUBJECT_ALREADY_EXISTS("RWS00024");

		private final String reasonCode;

		KnownRaveReasons(String reasonCode) {
			this.reasonCode = reasonCode;
		}

		public String getReasonCode() {
			return reasonCode;
		}
	}
}

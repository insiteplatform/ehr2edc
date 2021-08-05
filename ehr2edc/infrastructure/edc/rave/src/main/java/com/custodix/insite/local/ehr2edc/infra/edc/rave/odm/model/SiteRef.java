package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SiteRef")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SiteRef {
	@XmlAttribute(name = "LocationOID")
	private String locationOID;

	private SiteRef() {
		//JAXB deserialization
	}

	private SiteRef(Builder builder) {
		locationOID = builder.locationOID;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getLocationOID() {
		return locationOID;
	}

	public static final class Builder {
		private String locationOID;

		private Builder() {
		}

		public Builder withLocationOID(String locationOID) {
			this.locationOID = locationOID;
			return this;
		}

		public SiteRef build() {
			return new SiteRef(this);
		}
	}
}

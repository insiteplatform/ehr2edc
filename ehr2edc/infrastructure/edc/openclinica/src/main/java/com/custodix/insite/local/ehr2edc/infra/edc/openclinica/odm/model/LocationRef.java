package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public final class LocationRef {
	@XmlAttribute(name = "LocationOID")
	private String locationOID;

	private LocationRef(Builder builder) {
		locationOID = builder.locationOID;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String locationOID;

		private Builder() {
		}

		public Builder withLocationOID(String locationOID) {
			this.locationOID = locationOID;
			return this;
		}

		public LocationRef build() {
			return new LocationRef(this);
		}
	}
}

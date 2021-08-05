package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.Namespaces.DEFAULT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public final class AuditRecord {
	@XmlAttribute(name = "EditPoint")
	private String editPoint = EditPoint.MONITORING.attributeValue;
	@XmlElement(name = "UserRef",
				namespace = DEFAULT)
	private UserRef userRef;
	@XmlElement(name = "LocationRef",
				namespace = DEFAULT)
	private LocationRef locationRef;
	@XmlElement(name = "DateTimeStamp",
				namespace = DEFAULT)
	private String dateTimeStamp;
	@XmlElement(name = "ReasonForChange",
				namespace = DEFAULT)
	private String reasonForChange = "Import from EHR on behalf of user";

	private AuditRecord(Builder builder) {
		userRef = builder.userRef;
		locationRef = builder.locationRef;
		dateTimeStamp = builder.dateTimeStamp.truncatedTo(ChronoUnit.SECONDS)
				.toString();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private UserRef userRef;
		private LocationRef locationRef;
		private Instant dateTimeStamp;

		private Builder() {
		}

		public Builder withUserRef(UserRef userRef) {
			this.userRef = userRef;
			return this;
		}

		public Builder withLocationRef(LocationRef locationRef) {
			this.locationRef = locationRef;
			return this;
		}

		public Builder withDateTimeStamp(Instant dateTimeStamp) {
			this.dateTimeStamp = dateTimeStamp;
			return this;
		}

		public AuditRecord build() {
			return new AuditRecord(this);
		}
	}

	private enum EditPoint {
		MONITORING("Monitoring");

		private final String attributeValue;

		EditPoint(String attributeValue) {
			this.attributeValue = attributeValue;
		}
	}
}

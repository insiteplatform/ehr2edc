package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ODM")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ODM {
	@XmlElement(name = "ClinicalData")
	private List<ClinicalData> clinicalData = emptyList();

	@XmlAttribute(name = "ODMVersion")
	private String ODMVersion = "1.3";
	@XmlAttribute(name = "FileType")
	private String fileType = "Transactional";
	@XmlAttribute(name = "FileOID")
	private String fileOID = "Example-1";
	@XmlAttribute(name = "CreationDateTime")
	private String creationDateTime;

	private ODM() {
		// JAXB
	}

	private ODM(Builder builder) {
		clinicalData = builder.clinicalData;
		creationDateTime = builder.creationDateTime.truncatedTo(ChronoUnit.SECONDS)
				.toString();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private List<ClinicalData> clinicalData = emptyList();
		private Instant creationDateTime;

		private Builder() {
		}

		public Builder withClinicalData(ClinicalData clinicalData) {
			this.clinicalData = singletonList(clinicalData);
			return this;
		}

		public Builder withCreationDateTime(Instant creationDateTime) {
			this.creationDateTime = creationDateTime;
			return this;
		}

		public ODM build() {
			return new ODM(this);
		}
	}
}
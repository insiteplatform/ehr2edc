package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ODM",
				namespace = Namespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public final class ODM {
	@XmlElement(name = "ClinicalData",
				namespace = Namespaces.DEFAULT)
	private List<ClinicalData> clinicalData = emptyList();

	@XmlAttribute(name = "ODMVersion")
	private String ODMVersion = "1.3";
	@XmlAttribute(name = "FileType")
	private String FileType = "Transactional";
	@XmlAttribute(name = "FileOID")
	private String FileOID = "Example-1";
	@XmlAttribute(name = "CreationDateTime")
	private String CreationDateTime = LocalDateTime.now()
			.truncatedTo(ChronoUnit.SECONDS)
			.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

	private ODM() {
		//JAXB deserialization
	}

	private ODM(Builder builder) {
		clinicalData = builder.clinicalData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public List<ClinicalData> getClinicalData() {
		return clinicalData;
	}

	public static final class Builder {
		private List<ClinicalData> clinicalData = emptyList();

		private Builder() {
		}

		public Builder withClinicalData(ClinicalData clinicalData) {
			this.clinicalData = singletonList(clinicalData);
			return this;
		}

		public ODM build() {
			return new ODM(this);
		}
	}
}
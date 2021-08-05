package com.custodix.insite.local.ehr2edc.jackson.mixin.query;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
			  include = JsonTypeInfo.As.PROPERTY,
			  property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = DemographicQuery.class,
								   name = "demographic"),
				@JsonSubTypes.Type(value = LaboratoryQuery.class,
								   name = "laboratory"),
				@JsonSubTypes.Type(value = VitalSignQuery.class,
								   name = "vitalSign"),
				@JsonSubTypes.Type(value = MedicationQuery.class,
								   name = "medication")})
public abstract class QueryMixin {

}

package com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl;

public class QueryDsl {
	QueryDsl() {
	}

	public static LabValueQueryDsl labValueQuery() {
		return new LabValueQueryDsl();
	}

	public static DemographicQueryDsl demographicQuery() {
		return new DemographicQueryDsl();
	}

	public static VitalSignQueryDsl vitalSignQueryDsl() {
		return new VitalSignQueryDsl();
	}

	public static MedicationQueryDsl medicationQueryDsl() {
		return new MedicationQueryDsl();
	}
}

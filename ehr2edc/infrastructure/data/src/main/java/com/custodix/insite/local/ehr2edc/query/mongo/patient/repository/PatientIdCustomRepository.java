package com.custodix.insite.local.ehr2edc.query.mongo.patient.repository;

import java.util.List;

public interface PatientIdCustomRepository {
	List<String> findDistinctSources();
}

package com.custodix.insite.mongodb.export.patient.domain.repository;

import com.custodix.insite.mongodb.vocabulary.Namespace;

public interface PatientNamespaceRepository {
	boolean exists(Namespace namespace);
}
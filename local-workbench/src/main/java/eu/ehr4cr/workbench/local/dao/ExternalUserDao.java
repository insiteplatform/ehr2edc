package eu.ehr4cr.workbench.local.dao;

import java.util.Optional;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;

public interface ExternalUserDao {
	ExternalUser persist(ExternalUser user);

	Optional<ExternalUser> findForNameAndCompany(String name, String company);
}
package eu.ehr4cr.workbench.local.dao.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import eu.ehr4cr.workbench.local.dao.ExternalUserDao;
import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;

@Repository
class JpaExternalUserRepository extends AbstractJpaGenericRepository implements ExternalUserDao {

	@Override
	public ExternalUser persist(ExternalUser user) {
		return super.persist(user);
	}

	@Override
	public Optional<ExternalUser> findForNameAndCompany(String name, String company) {
		String query = "SELECT e FROM ExternalUser e WHERE e.name = :name AND e.companyName = :company";
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("company", company);
		return this.findSingleOptional(query, params);
	}
}
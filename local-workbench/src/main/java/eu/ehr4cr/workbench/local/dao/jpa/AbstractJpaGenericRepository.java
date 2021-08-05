package eu.ehr4cr.workbench.local.dao.jpa;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.GenericDao;
import eu.ehr4cr.workbench.local.dao.jpa.dto.PaginationParameters;

/**
 * Implementation of GenericDao with a JPA EntityManager.
 */
@Transactional("localTransactionManager")
public abstract class AbstractJpaGenericRepository implements GenericDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaGenericRepository.class);

	protected String entityNotFoundMessage = "No entity {0} found for id {1}.";

	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public <T> T get(Class<T> persistentClass, Long id) {
		return entityManager.find(persistentClass, id);
	}

	@Override
	public <T> T getProxy(Class<T> persistentClass, Long id) {
		return entityManager.getReference(persistentClass, id);
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	public <T> T merge(T entity) {
		entityManager.merge(entity);
		flush();
		return entity;
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	public <T> T persist(T entity) {
		entityManager.persist(entity);
		flush();
		return entity;
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	public <T> T refresh(T entity) {
		entityManager.refresh(entity);
		return entity;
	}

	@Override
	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	public void flush() {
		entityManager.flush();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public <T> void remove(T entity) {
		if (entityManager.contains(entity)) {
			entityManager.remove(entity);
		} else {
			entityManager.remove(entityManager.merge(entity));
		}
		flush();
	}

	@Override
	public <T> List<T> getAll(Class<T> persistentClass, int begin, int maxItems, String orderBy) {

		final String queryTemplate = MessageFormat.format("SELECT e from {0} e order by {1}", persistentClass.getName(),
				orderBy);
		return entityManager.createQuery(queryTemplate)
				.setFirstResult(begin)
				.setMaxResults(maxItems)
				.getResultList();
	}

	@Override
	public <T> long getCount(Class<T> persistentClass) {
		return (Long) entityManager.createQuery(String.format("SELECT COUNT(e) from %s e", persistentClass.getName()))
				.getSingleResult();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public <T> T save(T entity) {
		return persist(entity);
	}

	/**
	 * A generic find function for a collection of entities to be used by
	 * subclasses to execute a JPQL query with parameters.
	 *
	 * @param query
	 *            the java persistence query language query to execute.
	 * @param params
	 *            the named parameters of the query with their values.
	 * @param <T>
	 *            The type of entities that are expected to be returned.
	 * @return the result of the query as a collections of objects (can be
	 *         empty).
	 */
	protected <T> List<T> find(String query, Map<String, Object> params) {
		final Query q = entityManager.createQuery(query);
		if (params != null) {
			for (Map.Entry<String, Object> param : params.entrySet()) {
				q.setParameter(param.getKey(), param.getValue());
			}
		}
		return q.getResultList();
	}

	/**
	 * A generic find function for a collection of entities to be used by
	 * subclasses to execute a JPQL query with parameters. This method only
	 * returns a subset of the resultset
	 *
	 * @param query
	 *            the java persistence query language query to execute.
	 * @param params
	 *            the named parameters of the query with their values.
	 * @param parameters
	 *            The size of each page of the query and the maximum number of
	 *            results that can be returned and the page number that is
	 *            requested. This is 0-based
	 * @param <T>
	 *            The type of entities that are expected to be returned.
	 * @return The paged list of entities
	 */
	protected <T> List<T> findPaged(String query, Map<String, Object> params, PaginationParameters parameters) {
		final Query q = entityManager.createQuery(query);
		if (params != null) {
			for (Map.Entry<String, Object> param : params.entrySet()) {
				q.setParameter(param.getKey(), param.getValue());
			}
		}
		q.setFirstResult(parameters.getPageSize() * parameters.getPageNr());
		q.setMaxResults(parameters.getPageSize());

		return q.getResultList();
	}

	/**
	 * A generic find function for a single entity to be used by subclasses to
	 * execute a JPQL query with parameters.
	 *
	 * @param query
	 *            the java persistence query language query to execute.
	 * @param params
	 *            the named parameters of the query with their values.
	 * @param <T>
	 *            The type of entities that are expected to be returned.
	 * @return the result of the query as a single object (never null).
	 */
	protected <T> T findSingle(String query, Map<String, Object> params) {
		final Query q = entityManager.createQuery(query);
		for (Map.Entry<String, Object> param : params.entrySet()) {
			q.setParameter(param.getKey(), param.getValue());
		}
		q.setMaxResults(1);
		return (T) q.getSingleResult();
	}

	protected <T> T findSingleOrNull(String query, Map<String, Object> params) {
		try {
			return findSingle(query, params);
		} catch (NoResultException ex) {
			LOGGER.debug("No entity found for query {} with params {}",query, params);
			return null;
		}
	}

	protected <T> Optional<T> findSingleOptional(String query, Map<String, Object> params) {
		try {
			return Optional.of(findSingle(query, params));
		} catch (NoResultException ex) {
			LOGGER.debug("No values found for query {} with params {}",query, params);
			return Optional.empty();
		}
	}
}

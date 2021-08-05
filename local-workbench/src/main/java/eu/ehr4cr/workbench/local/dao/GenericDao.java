package eu.ehr4cr.workbench.local.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao for generic entities.
 */
public interface GenericDao {

	EntityManager getEntityManager();

	/**
	 * The returned entity is not a proxy and will be initialized.
	 * 
	 * @param <T>
	 *            generic
	 * @param persistentClass
	 *            persistentClass
	 * @param id
	 *            entity-id
	 * @return a specific Entity
	 */
	<T> T get(Class<T> persistentClass, Long id);

	/**
	 * The returned object may be a proxy, initialized at first use.
	 * 
	 * @param <T>
	 *            generic
	 * @param persistentClass
	 *            persistentClass
	 * @param id
	 *            entity-id
	 * @return a specific Entity
	 */
	<T> T getProxy(Class<T> persistentClass, Long id);

	/**
	 * @param <T>
	 *            generic
	 * @param persistentClass
	 *            persistentClass
	 * @param begin
	 *            begin
	 * @param maxItems
	 *            maxItems
	 * @param orderBy
	 *            orderBy
	 * @return all entities of a specific class with the given paging parameters
	 */
	<T> List<T> getAll(Class<T> persistentClass, int begin, int maxItems, String orderBy);

	/**
	 * @param <T>
	 *            generic
	 * @param persistenClass
	 *            persistenClass
	 * @return the amount of entities of the given class
	 */
	<T> long getCount(Class<T> persistenClass);

	/**
	 * Merges the specific entity with the current session.
	 * 
	 * @param <T>
	 *            generic
	 * @param entity
	 *            entity
	 * @return entity
	 */
	<T> T merge(T entity);

	/**
	 * Persists the specific entity.
	 * 
	 * @param <T>
	 *            generic
	 * @param entity
	 *            entity
	 * @return entity
	 */
	<T> T persist(T entity);

	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	<T> T refresh(T entity);

	@Transactional(value = "localTransactionManager",
				   propagation = Propagation.MANDATORY)
	void flush();

	/**
	 * Removes the given entity.
	 * 
	 * @param <T>
	 *            generic
	 * @param entity
	 *            entity
	 */
	<T> void remove(T entity);

	<T> T save(T entity);
}
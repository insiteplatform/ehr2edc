package eu.ehr4cr.workbench.local.dao.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.CompositeUserType;

public abstract class ImmutableCompositeType<T> implements CompositeUserType {
	private final Class<T> clazz;

	protected ImmutableCompositeType(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public final Object getPropertyValue(Object component, int property)
			throws HibernateException {
		return getProperty(clazz.cast(component), property);
	}

	@Override
	public final void setPropertyValue(Object component, int property, Object value)
			throws HibernateException {
		throw new UnsupportedOperationException("Cannot set property on immutable object");
	}

	protected abstract Object getProperty(T component, int property);

	@Override
	public final Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		return get(rs, names, session, owner);
	}

	@Override
	public final void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws SQLException {
		set(st, clazz.cast(value), index, session);
	}

	protected abstract T get(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws SQLException;

	protected abstract void set(PreparedStatement st, T value, int index, SharedSessionContractImplementor session)
			throws SQLException;

	@Override
	public final Class<T> returnedClass() {
		return clazz;
	}

	@Override
	public final boolean equals(Object x, Object y) {
		return Objects.equals(x, y);
	}

	@Override
	public final int hashCode(Object x) {
		return x.hashCode();
	}

	@Override
	public final Object deepCopy(Object value) {
		return value;
	}

	@Override
	public final boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value, SharedSessionContractImplementor sharedSessionContractImplementor)
			throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, SharedSessionContractImplementor sharedSessionContractImplementor,
			Object o) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, SharedSessionContractImplementor sharedSessionContractImplementor,
			Object owner) throws HibernateException {
		return original;
	}
}
package eu.ehr4cr.workbench.local.dao.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public abstract class ImmutableType<T> implements UserType {

	private final Class<T> clazz;

	protected ImmutableType(Class<T> clazz) {
		this.clazz = clazz;
	}

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
	public final Serializable disassemble(Object o) {
		return (Serializable) o;
	}

	@Override
	public final Object assemble(Serializable cached, Object owner) {
		return cached;
	}

	@Override
	public final Object replace(Object o, Object target, Object owner) {
		return o;
	}
}
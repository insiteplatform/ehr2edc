package eu.ehr4cr.workbench.local.dao.types;

import static org.hibernate.type.StandardBasicTypes.LONG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

import eu.ehr4cr.workbench.local.vocabulary.clinical.FilterIdentifier;

public class FilterIdentifierType extends ImmutableCompositeType<FilterIdentifier> {
	public FilterIdentifierType() {
		super(FilterIdentifier.class);
	}

	@Override
	public String[] getPropertyNames() {
		return new String[] { "filterId", "versionId" };
	}

	@Override
	public Type[] getPropertyTypes() {
		return new Type[] { LONG, LONG };
	}

	@Override
	protected Object getProperty(FilterIdentifier filterIdentifier, int property) {
		if (filterIdentifier == null)
			return null;
		else {
			if (property == 0) {
				return filterIdentifier.getFilterId();
			} else if (property == 1) {
				return filterIdentifier.getVersionId();
			} else {
				return null;
			}
		}
	}

	@Override
	public FilterIdentifier get(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		long filterId = rs.getLong(names[0]);
		long versionId = rs.getLong(names[1]);
		if (rs.wasNull()) {
			return null;
		}
		return FilterIdentifier.newBuilder()
				.withFilterId(filterId)
				.withVersionId(versionId)
				.build();
	}

	@Override
	public void set(PreparedStatement st, FilterIdentifier value, int index, SharedSessionContractImplementor session)
			throws SQLException {
		if (value == null) {
			st.setNull(index, Types.BIGINT);
			st.setNull(index + 1, Types.BIGINT);
		} else {
			st.setLong(index, value.getFilterId());
			st.setLong(index + 1, value.getVersionId());
		}
	}
}
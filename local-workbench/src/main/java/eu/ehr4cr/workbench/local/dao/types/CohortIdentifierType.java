package eu.ehr4cr.workbench.local.dao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import eu.ehr4cr.workbench.local.vocabulary.CohortIdentifier;

public class CohortIdentifierType extends ImmutableType<CohortIdentifier> {
	public CohortIdentifierType() {
		super(CohortIdentifier.class);
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.BIGINT };
	}

	@Override
	public CohortIdentifier get(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		long value = rs.getLong(names[0]);
		if(rs.wasNull()) {
			return null;
		}
		return CohortIdentifier.of(value);
	}

	@Override
	public void set(PreparedStatement st, CohortIdentifier value, int index, SharedSessionContractImplementor session)
			throws SQLException {
		if (value == null) {
			st.setNull(index, Types.BIGINT);
		} else {
			st.setLong(index, value.getId());
		}
	}
}
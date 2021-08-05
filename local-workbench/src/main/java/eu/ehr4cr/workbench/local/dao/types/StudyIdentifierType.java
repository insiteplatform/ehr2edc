package eu.ehr4cr.workbench.local.dao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;

public class StudyIdentifierType extends ImmutableType<StudyIdentifier> {
	public StudyIdentifierType() {
		super(StudyIdentifier.class);
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.BIGINT };
	}

	@Override
	public StudyIdentifier get(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		long value = rs.getLong(names[0]);
		if(rs.wasNull()) {
			return null;
		}
		return StudyIdentifier.of(value);
	}

	@Override
	public void set(PreparedStatement st, StudyIdentifier value, int index, SharedSessionContractImplementor session)
			throws SQLException {
		if (value == null) {
			st.setNull(index, Types.BIGINT);
		} else {
			st.setLong(index, value.getId());
		}
	}
}
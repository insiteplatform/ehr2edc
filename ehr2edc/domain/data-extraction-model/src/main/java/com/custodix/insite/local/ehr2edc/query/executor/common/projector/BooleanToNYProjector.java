package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class BooleanToNYProjector implements Projector<String, Boolean> {

	private static final String NAME = "Boolean to NY code";

	@Override
	public Optional<String> project(Optional<Boolean> value, ProjectionContext context) {
		NY ny = value.map(booleanValue -> booleanValue ? NY.YES : NY.NO)
				.orElse(NY.UNKNOWN);
		return Optional.of(ny)
				.map(NY::getCode);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private enum NY {
		YES("Y"),
		NO("N"),
		NOT_AVAILABLE("NA"),
		UNKNOWN("U");

		private final String code;

		NY(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}

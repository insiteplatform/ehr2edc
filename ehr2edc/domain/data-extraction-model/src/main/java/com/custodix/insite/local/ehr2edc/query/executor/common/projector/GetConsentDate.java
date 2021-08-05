package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.time.LocalDate;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class GetConsentDate implements Projector<LocalDate, HasSubjectId> {

	private static final String NAME = "Date of consent";

	@Override
	public Optional<LocalDate> project(Optional<HasSubjectId> value, ProjectionContext context) {
		return Optional.ofNullable(context.getConsentDate());
	}

	@Override
	public String getName() {
		return NAME;
	}
}
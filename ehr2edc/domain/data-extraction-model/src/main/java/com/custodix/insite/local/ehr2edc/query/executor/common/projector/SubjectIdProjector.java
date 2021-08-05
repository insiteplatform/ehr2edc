package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;

public final class SubjectIdProjector implements Projector<String, HasSubjectId> {

	private static final String NAME = "Subject id";

	@Override
	public Optional<String> project(Optional<HasSubjectId> value, ProjectionContext context) {
		return Optional.ofNullable(context.getEdcSubjectReference())
				.map(EDCSubjectReference::getId);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
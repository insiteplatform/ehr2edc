package eu.ehr4cr.workbench.local.usecases.model.mappers;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ProtocolVersionScreeningFilter;
import eu.ehr4cr.workbench.local.usecases.model.dto.ProtocolVersionFilterDto;

public final class ProtocolVersionFilterMapper {
	private ProtocolVersionFilterMapper() {
	}

	public static ProtocolVersionFilterDto mapFilter(ProtocolVersionScreeningFilter filter) {
		return ProtocolVersionFilterDto.builder()
				.withArchived(filter.getArchived())
				.withCreationDate(filter.getCreationDate())
				.withCreator(filter.getCreator())
				.withFromSponsor(filter.getFromSponsor())
				.withId(filter.getId())
				.withJsonContent(filter.getJsonContent())
				.withSponsor(filter.getSponsor())
				.withVersion(filter.getVersion())
				.withFormattedJsonContent(filter.getFormattedJsonContent())
				.build();
	}
}
package eu.ehr4cr.workbench.local.usecases.model.mappers;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ProtocolVersion;
import eu.ehr4cr.workbench.local.usecases.model.dto.ProtocolVersionDto;

public final class ProtocolVersionMapper {
	private ProtocolVersionMapper() {
	}

	public static ProtocolVersionDto mapVersion(ProtocolVersion version) {
		return ProtocolVersionDto.builder()
				.withId(version.getId())
				.withVersion(version.getVersion())
				.build();
	}
}
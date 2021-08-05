package eu.ehr4cr.workbench.local.usecases.model.mappers;

import eu.ehr4cr.workbench.local.model.clinicalStudy.PrincipalInvestigator;
import eu.ehr4cr.workbench.local.usecases.model.dto.InvestigatorDto;

public final class InvestigatorMapper {

	private InvestigatorMapper() {
	}

	public static InvestigatorDto mapInvestigator(PrincipalInvestigator investigator) {
		return InvestigatorDto.builder()
				.withId(investigator.getId())
				.withOccupied(investigator.getOccupied())
				.withUser(investigator.getUser())
				.build();
	}

}
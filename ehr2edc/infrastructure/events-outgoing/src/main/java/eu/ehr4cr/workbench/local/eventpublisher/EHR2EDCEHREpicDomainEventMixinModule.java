package eu.ehr4cr.workbench.local.eventpublisher;

import com.custodix.insite.local.ehr2edc.ehr.epic.domain.event.EpicPortalPatientRegistered;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class EHR2EDCEHREpicDomainEventMixinModule {

	private EHR2EDCEHREpicDomainEventMixinModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addEventMixins(simpleModule);
		return simpleModule;
	}

	private static void addEventMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(EpicPortalPatientRegistered.class, EHR2EDCEHREpicDomainEventMixin.class);
	}

	@JsonDeserialize(builder = EpicPortalPatientRegistered.Builder.class)
	interface EHR2EDCEHREpicDomainEventMixin {}

}

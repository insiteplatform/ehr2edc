package eu.ehr4cr.workbench.local.eventpublisher;

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class EHRDomainEventMixinModule {

	private EHRDomainEventMixinModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addEventMixins(simpleModule);
		return simpleModule;
	}

	private static void addEventMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(EHRSubjectRegistrationStatusUpdated.class, EHRSubjectRegistrationStatusUpdatedMixin.class);
	}

	@JsonDeserialize(builder = EHRSubjectRegistrationStatusUpdated.Builder.class)
	interface EHRSubjectRegistrationStatusUpdatedMixin {}

}

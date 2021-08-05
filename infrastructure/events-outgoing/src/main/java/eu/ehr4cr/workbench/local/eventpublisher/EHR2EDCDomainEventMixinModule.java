package eu.ehr4cr.workbench.local.eventpublisher;

import com.custodix.insite.local.ehr2edc.events.SubjectCreated;
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class EHR2EDCDomainEventMixinModule {

	private EHR2EDCDomainEventMixinModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addEventMixins(simpleModule);
		return simpleModule;
	}

	private static void addEventMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(SubjectCreated.class, SubjectCreatedMixin.class);
		simpleModule.setMixInAnnotation(SubjectRegisteredEvent.class, SubjectRegisteredEventMixin.class);
		simpleModule.setMixInAnnotation(SubjectDeregisteredEvent.class, SubjectDeregisteredEventMixin.class);
		simpleModule.setMixInAnnotation(ExternalEDCConnection.class, ExternalEDCConnectionMixin.class);
	}

	@JsonDeserialize(builder = ExternalEDCConnection.Builder.class)
	interface ExternalEDCConnectionMixin {}

	@JsonDeserialize(builder = SubjectCreated.Builder.class)
	interface SubjectCreatedMixin {}

	@JsonDeserialize(builder = SubjectRegisteredEvent.Builder.class)
	interface SubjectRegisteredEventMixin {}

	@JsonDeserialize(builder = SubjectDeregisteredEvent.Builder.class)
	interface SubjectDeregisteredEventMixin {}
}

package eu.ehr4cr.workbench.local.eventpublisher;

import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class MongoMigratorDomainEventMixinModule {

	private MongoMigratorDomainEventMixinModule() {
	}

	public static SimpleModule create() {
		SimpleModule simpleModule = new SimpleModule();
		addEventMixins(simpleModule);
		return simpleModule;
	}

	private static void addEventMixins(SimpleModule simpleModule) {
		simpleModule.setMixInAnnotation(ExportPatientStarting.class, ExportPatientStartedMixin.class);
		simpleModule.setMixInAnnotation(ExportPatientEnded.class, ExportPatientEndedMixin.class);
		simpleModule.setMixInAnnotation(ExportPatientFailed.class, ExportPatientFailedMixin.class);
	}

	@JsonDeserialize(builder = ExportPatientStarting.Builder.class)
	interface ExportPatientStartedMixin {}

	@JsonDeserialize(builder = ExportPatientEnded.Builder.class)
	interface ExportPatientEndedMixin {}

	@JsonDeserialize(builder = ExportPatientFailed.Builder.class)
	interface ExportPatientFailedMixin {}

}

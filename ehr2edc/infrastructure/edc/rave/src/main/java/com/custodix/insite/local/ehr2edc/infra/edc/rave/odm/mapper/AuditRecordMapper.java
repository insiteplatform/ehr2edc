package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.mapper;

import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.AuditRecord;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.LocationRef;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.UserRef;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

final class AuditRecordMapper {
	private final SubmittedEvent submittedEvent;
	private final ExternalEDCConnection connection;

	AuditRecordMapper(SubmittedEvent submittedEvent, ExternalEDCConnection connection) {
		this.submittedEvent = submittedEvent;
		this.connection = connection;
	}

	AuditRecord map() {
		return AuditRecord.newBuilder()
				.withUserRef(mapUserRef())
				.withLocationRef(mapLocationRef())
				.withDateTimeStamp(submittedEvent.getSubmittedDate())
				.build();
	}

	private UserRef mapUserRef() {
		return UserRef.newBuilder()
				.withUserOID(submittedEvent.getSubmitter()
						.getId())
				.build();
	}

	private LocationRef mapLocationRef() {
		return LocationRef.newBuilder()
				.withLocationOID(connection.getExternalSiteId()
						.getId())
				.build();
	}
}

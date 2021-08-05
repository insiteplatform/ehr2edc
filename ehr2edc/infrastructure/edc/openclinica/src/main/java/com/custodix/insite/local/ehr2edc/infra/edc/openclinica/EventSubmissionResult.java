package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.EventSubmissionResultRecord.Type.EVENT;
import static com.custodix.insite.local.ehr2edc.infra.edc.openclinica.EventSubmissionResultRecord.Type.SUBJECT;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class EventSubmissionResult {
	private final List<EventSubmissionResultRecord> record;

	EventSubmissionResult(List<EventSubmissionResultRecord> record) {
		this.record = record;
	}

	Optional<EventSubmissionResultRecord> findGeneralFailure() {
		return failed(SUBJECT, EVENT).findFirst();
	}

	List<EventSubmissionResultRecord> getFailures(EventSubmissionResultRecord.Type type) {
		return failed(type).collect(Collectors.toList());
	}

	private Stream<EventSubmissionResultRecord> failed(EventSubmissionResultRecord.Type... types) {
		return record.stream()
				.filter(EventSubmissionResultRecord::isFailed)
				.filter(i -> i.hasOneOfTypes(types));
	}
}

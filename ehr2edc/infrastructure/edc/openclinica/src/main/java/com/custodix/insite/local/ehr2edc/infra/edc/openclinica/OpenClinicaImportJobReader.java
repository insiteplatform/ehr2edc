package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.io.StringReader;
import java.util.List;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

class OpenClinicaImportJobReader {
	private final OpenClinicaImportJobClient importJobClient;

	OpenClinicaImportJobReader(OpenClinicaImportJobClient importJobClient) {
		this.importJobClient = importJobClient;
	}

	EventSubmissionResult readJob(String jobUUID, AuthenticationToken authenticationToken) {
		String response = importJobClient.getJobReport(jobUUID, authenticationToken);
		return mapResponse(response.trim());
	}

	private EventSubmissionResult mapResponse(String response) {
		CsvToBean<EventSubmissionResultRecord> csvToBean = new CsvToBeanBuilder<EventSubmissionResultRecord>(
				new StringReader(response)).withType(EventSubmissionResultRecord.class)
				.build();
		List<EventSubmissionResultRecord> records = csvToBean.parse();
		return new EventSubmissionResult(records);
	}
}

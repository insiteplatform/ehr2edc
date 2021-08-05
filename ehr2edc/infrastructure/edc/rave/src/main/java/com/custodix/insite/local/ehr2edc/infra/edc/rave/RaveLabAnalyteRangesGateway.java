package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import com.custodix.insite.local.ehr2edc.LabAnalyteRangesGateway;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

class RaveLabAnalyteRangesGateway implements LabAnalyteRangesGateway {
	private final RaveRestClient restClient;

	RaveLabAnalyteRangesGateway(final RaveRestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	public List<LabName> findActiveLabs(ExternalEDCConnection externalEDCConnection) {
		if (externalEDCConnection.isEnabled()) {
			String responseBody = restClient.get(externalEDCConnection.getClinicalDataURI(),
					externalEDCConnection.getUsername(), externalEDCConnection.getPassword());
			return extractLabNamesFrom(responseBody, externalEDCConnection.getExternalSiteId());
		}
		return emptyList();
	}

	private List<LabName> extractLabNamesFrom(String responseBody, ExternalSiteId externalSiteId) {
		try (CSVParser parser = parserFor(responseBody)) {
			return extractLabNamesUsing(parser, externalSiteId);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private List<LabName> extractLabNamesUsing(CSVParser parser, ExternalSiteId externalSiteId) throws IOException {
		return parser.getRecords()
				.stream()
				.filter(record -> record.get("SiteNumber")
						.equals(externalSiteId.getId()))
				.map(record -> record.get("LabName"))
				.distinct()
				.map(LabName::of)
				.collect(toList());
	}

	private CSVParser parserFor(final String body) throws IOException {
		return new CSVParser(new StringReader(body), CSVFormat.DEFAULT.withFirstRecordAsHeader());
	}
}

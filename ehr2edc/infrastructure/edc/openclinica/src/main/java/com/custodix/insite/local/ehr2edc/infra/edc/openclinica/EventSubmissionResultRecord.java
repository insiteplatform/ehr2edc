package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.opencsv.bean.CsvBindByName;

public final class EventSubmissionResultRecord {
	@CsvBindByName(column = "SubjectKey")
	private String subjectKey;
	@CsvBindByName(column = "StudyEventOID")
	private String studyEventOID;
	@CsvBindByName(column = "FormOID")
	private String formOID;
	@CsvBindByName(column = "ItemGroupOID")
	private String itemGroupOID;
	@CsvBindByName(column = "ItemOID")
	private String itemOID;
	@CsvBindByName(column = "Status")
	private String status;
	@CsvBindByName(column = "Message")
	private String errorCode;

	boolean isFailed() {
		return Status.of(status) == Status.FAILED;
	}

	boolean hasOneOfTypes(Type... types) {
		return asList(types).contains(Type.of(this));
	}

	String getFormOID() {
		return formOID;
	}

	String getItemGroupOID() {
		return itemGroupOID;
	}

	String getItemOID() {
		return itemOID;
	}

	String getErrorCode() {
		return errorCode;
	}

	enum Type {
		SUBJECT {
			@Override
			boolean hasType(EventSubmissionResultRecord record) {
				return isNotBlank(record.subjectKey) && isBlank(record.studyEventOID) && isBlank(record.formOID)
						&& isBlank(record.itemGroupOID) && isBlank(record.itemOID);
			}
		},
		EVENT {
			@Override
			boolean hasType(EventSubmissionResultRecord record) {
				return isNotBlank(record.subjectKey) && isNotBlank(record.studyEventOID) && isBlank(record.formOID)
						&& isBlank(record.itemGroupOID) && isBlank(record.itemOID);
			}
		},
		FORM {
			@Override
			boolean hasType(EventSubmissionResultRecord record) {
				return isNotBlank(record.subjectKey) && isNotBlank(record.studyEventOID) && isNotBlank(record.formOID)
						&& isBlank(record.
						itemGroupOID) && isBlank(record.itemOID);
			}
		},
		ITEM_GROUP {
			@Override
			boolean hasType(EventSubmissionResultRecord record) {
				return isNotBlank(record.subjectKey) && isNotBlank(record.studyEventOID) && isNotBlank(record.formOID)
						&& isNotBlank(record.
						itemGroupOID) && isBlank(record.itemOID);
			}
		},
		ITEM {
			@Override
			boolean hasType(EventSubmissionResultRecord record) {
				return isNotBlank(record.subjectKey) && isNotBlank(record.studyEventOID) && isNotBlank(record.formOID)
						&& isNotBlank(record.
						itemGroupOID) && isNotBlank(record.itemOID);
			}
		};

		static Type of(EventSubmissionResultRecord record) {
			return stream(values()).filter(t -> t.hasType(record))
					.findFirst()
					.orElseThrow(() -> new SystemException("No matching type for result record"));
		}

		abstract boolean hasType(EventSubmissionResultRecord record);
	}

	enum Status {
		FAILED("Failed"),
		INSERTED("Inserted"),
		UPDATED("Updated"),
		NO_CHANGE("No Change");

		private final String value;

		Status(String value) {
			this.value = value;
		}

		static Status of(String value) {
			return stream(values()).filter(e -> e.matches(value))
					.findFirst()
					.orElseThrow(
							() -> new SystemException(String.format("Unexpected item submission status '%s'", value)));
		}

		boolean matches(String value) {
			return this.value.equalsIgnoreCase(value);
		}
	}
}

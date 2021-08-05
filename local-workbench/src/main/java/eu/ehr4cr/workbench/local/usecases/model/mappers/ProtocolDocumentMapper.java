package eu.ehr4cr.workbench.local.usecases.model.mappers;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ProtocolVersionDocument;
import eu.ehr4cr.workbench.local.usecases.model.dto.ProtocolDocumentDto;

public final class ProtocolDocumentMapper {
	private static final Long INVALID_FILE_SIZE = 1024L;

	private ProtocolDocumentMapper() {
	}

	public static final ProtocolDocumentDto mapDocument(ProtocolVersionDocument document) {
		return ProtocolDocumentDto.builder()
				.withId(document.getId())
				.withDocumentContent(document.getFile())
				.withFileName(document.getFileName())
				.withFileSizeInBytes(document.getFileSize())
				.withFileSize(getFileSizeToDisplay(document))
				.withFileType(document.getFileType())
				.withVersion(document.getProtocolVersion())
				.build();
	}

	private static Long getFileSizeToDisplay(ProtocolVersionDocument document) {
		if (isSizeInvalid(document)) {
			return INVALID_FILE_SIZE;
		} else {
			return document.getFileSize();
		}
	}

	private static boolean isSizeInvalid(ProtocolVersionDocument document) {
		return document.getFileSize() != null && document.getFileSize() < INVALID_FILE_SIZE;
	}
}
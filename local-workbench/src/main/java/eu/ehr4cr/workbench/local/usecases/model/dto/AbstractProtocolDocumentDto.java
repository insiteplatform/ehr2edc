package eu.ehr4cr.workbench.local.usecases.model.dto;

import javax.validation.constraints.Min;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ProtocolVersion;

public interface AbstractProtocolDocumentDto {
	@Min(1) Long getId();

	ProtocolVersion getVersion();

	byte[] getDocumentContent();

	String getFileType();

	String getFileName();

	Long getFileSizeInBytes();

	Long getFileSize();
}
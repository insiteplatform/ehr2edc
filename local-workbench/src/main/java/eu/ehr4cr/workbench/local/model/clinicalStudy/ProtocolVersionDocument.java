package eu.ehr4cr.workbench.local.model.clinicalStudy;

import javax.persistence.*;
import java.net.URLConnection;

@Entity
@Table(name = "clinical_study_protocol_version_document")
public class ProtocolVersionDocument {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne()
	private ProtocolVersion protocolVersion;

	@Lob
	private byte[] file;

	private String fileType;
	private String fileName;

	// Size in bytes
	private Long fileSize;

	private ProtocolVersionDocument() {
		// JPA
	}

	public ProtocolVersionDocument(ProtocolVersion protocolVersion, String name, byte[] content) {
		this.file = content;
		this.fileName = name;
		this.fileType = URLConnection.guessContentTypeFromName(name);
		this.fileSize = Long.valueOf(content.length);
		this.protocolVersion = protocolVersion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	public byte[] getFile() {
		return file;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}
}

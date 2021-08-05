package eu.ehr4cr.workbench.local.model.export;

import static com.google.common.net.MediaType.CSV_UTF_8;
import static com.google.common.net.MediaType.OOXML_SHEET;

import eu.ehr4cr.workbench.local.export.ExporterFormatType;

public enum ExportResultFormatType {
	CSV("CSV", CSV_UTF_8.toString(), ExporterFormatType.CSV),
	EXCEL("EXCEL", OOXML_SHEET.toString(), ExporterFormatType.EXCEL);

	private final String value;
	private final String mimeType;
	private final ExporterFormatType exporterFormatType;

	ExportResultFormatType(String value, String mimeType, ExporterFormatType exporterFormatType) {
		this.value = value;
		this.mimeType = mimeType;
		this.exporterFormatType = exporterFormatType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public ExporterFormatType getExporterFormatType() {
		return exporterFormatType;
	}

	@Override
	public String toString() {
		return value;
	}
}

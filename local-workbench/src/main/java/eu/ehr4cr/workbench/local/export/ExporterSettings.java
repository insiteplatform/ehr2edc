package eu.ehr4cr.workbench.local.export;

import java.io.File;

public interface ExporterSettings {
	File getDir();

	String getFileName();

	int getMaxFileCount();

	int getMaxRowsPerFile();

	ExportRow getHeaderRow();
}

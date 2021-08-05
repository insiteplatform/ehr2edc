package eu.ehr4cr.workbench.local.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Exporter {
	protected final File subDir;
	protected final String fileName;
	protected final int maxRowsPerFile;
	protected final int maxFileCount;
	protected final ExportRow headerRow;
	protected List<File> files;
	protected File currentFile;
	protected int currentFileRowCount;
	protected int totalRowCount;


	public Exporter(ExporterSettings exporterSettings) {
		this.subDir = exporterSettings.getDir();
		this.fileName = exporterSettings.getFileName();
		this.maxFileCount = exporterSettings.getMaxFileCount();
		this.maxRowsPerFile = exporterSettings.getMaxRowsPerFile();
		this.headerRow = exporterSettings.getHeaderRow();
		files = new ArrayList<>();
		currentFileRowCount = 0;
		totalRowCount = 0;
	}

	public List<File> getFiles() {
		return files;
	}

	public int getTotalRowCount() {
		return totalRowCount;
	}

	public int getMaxRowsPerFile() {
		return maxRowsPerFile;
	}

	public int getMaxFileCount() {
		return maxFileCount;
	}

	protected void incrementRowCounts() {
		currentFileRowCount++;
		totalRowCount++;
	}

	public abstract void closeWriter() throws IOException;

	public abstract void addRow(ExportRow row);

}

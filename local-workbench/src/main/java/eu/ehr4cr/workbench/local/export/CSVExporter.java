package eu.ehr4cr.workbench.local.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

public class CSVExporter extends Exporter {
	private final Logger LOGGER = LoggerFactory.getLogger(CSVExporter.class);
	private CSVWriter writer;

	public CSVExporter(ExporterSettings exporterSettings) {
		super(exporterSettings);
		startNewFile();
	}

	@Override
	public void addRow(ExportRow row) {
		try {
			doAddRow(row);
			startNewFileIfLimitReached();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void doAddRow(ExportRow row) throws IOException {
		String[] rowValues = createRowArray(row);
		writer.writeNext(rowValues);
		incrementRowCounts();
		writer.flush();
	}

	private void startNewFile() {
		if (files.size() < maxFileCount) {
			try {
				doStartNewFile();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			throw new FileCountLimitReachedException();
		}
	}

	private void doStartNewFile() throws IOException {
		LOGGER.debug("Starting new file. Currently {} files already processed with {} total rows.", files.size(),
				totalRowCount);
		closeWriter();
		currentFile = File.createTempFile(fileName, ".csv", subDir);
		currentFileRowCount = 0;
		files.add(currentFile);
		openWriter();
		addHeaderRow();
	}

	private void startNewFileIfLimitReached() {
		if (currentFileRowCount >= maxRowsPerFile) {
			startNewFile();
		}
	}

	private void openWriter() throws IOException {
		writer = new CSVWriter(new FileWriter(currentFile, true));
	}

	@Override
	public void closeWriter() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

	private void addHeaderRow() {
		String[] rowValues = createRowArray(headerRow);
		writer.writeNext(rowValues);
		incrementRowCounts();
	}

	private String[] createRowArray(ExportRow row) {
		return row.getCells()
				.stream()
				.map(exportCell -> exportCell.getValue()
						.orElse(""))
				.map(String::valueOf)
				.toArray(String[]::new);
	}

}

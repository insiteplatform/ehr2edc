package eu.ehr4cr.workbench.local.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelExporter extends Exporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExporter.class);

	private SXSSFWorkbook workbook;
	private Sheet sheet;
	private FileOutputStream fileOut;

	public ExcelExporter(ExporterSettings exporterSettings) {
		super(exporterSettings);
		startNewFile();
	}

	@Override
	public void addRow(ExportRow row) {
		doAddRow(row);
		startNewFileIfLimitReached();
	}

	private void doAddRow(ExportRow row) {
		Row sheetRow = sheet.createRow(currentFileRowCount);
		List<ExportCell> cells = row.getCells();
		for (int i = 0; i < cells.size(); i++) {
			Cell cell = sheetRow.createCell(i);
			setCellValue(cell, cells.get(i));
		}
		incrementRowCounts();
	}

	private void setCellValue(Cell cell, ExportCell exportCell) {
		ExportCell.ExportCellType type = exportCell.getType();
		Optional<Object> value = exportCell.getValue();
		if (value.isPresent()) {
			setCellValue(cell, type, value.get());
		} else {
			cell.setCellValue("");
		}
	}

	private void setCellValue(Cell cell, ExportCell.ExportCellType type, Object value) {
		switch (type) {
		case NUMBER:
			cell.setCellValue(((Number) value).doubleValue());
			break;
		case TEXT:
		default:
			cell.setCellValue(String.valueOf(value));
			break;
		}
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
		currentFile = File.createTempFile(fileName, ".xlsx", subDir);
		currentFileRowCount = 0;
		files.add(currentFile);
		workbook = new SXSSFWorkbook(1000);
		sheet = workbook.createSheet();
		openWriter();
		addHeaderRow();
	}

	private void startNewFileIfLimitReached() {
		if (currentFileRowCount >= maxRowsPerFile) {
			startNewFile();
		}
	}

	public void openWriter() throws IOException {
		fileOut = new FileOutputStream(currentFile);
	}

	@Override
	public void closeWriter() throws IOException {
		if (workbook != null) {
			workbook.write(fileOut);
			fileOut.close();
			workbook.dispose();
		}
	}

	public void addHeaderRow() {
		doAddRow(headerRow);
	}

}

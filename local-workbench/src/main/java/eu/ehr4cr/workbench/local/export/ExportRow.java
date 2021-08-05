package eu.ehr4cr.workbench.local.export;

import java.util.ArrayList;
import java.util.List;

public class ExportRow {
	private final List<ExportCell> cells;

	public ExportRow() {
		cells = new ArrayList<>();
	}

	public List<ExportCell> getCells() {
		return cells;
	}

	public void addCell(Object value, ExportCell.ExportCellType type) {
		cells.add(new ExportCell(value, type));
	}
}

package eu.ehr4cr.workbench.local.export;

import java.util.Optional;

public class ExportCell {
	private final Object value;
	private final ExportCellType type;

	public ExportCell(Object value, ExportCellType type) {
		this.value = value;
		this.type = type;
	}

	public Optional<Object> getValue() {
		return Optional.ofNullable(value);
	}

	public ExportCellType getType() {
		return type;
	}

	public enum ExportCellType {
		TEXT, NUMBER
	}
}

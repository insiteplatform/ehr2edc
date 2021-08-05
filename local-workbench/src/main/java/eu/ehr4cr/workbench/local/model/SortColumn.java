package eu.ehr4cr.workbench.local.model;

/**
 * Sort column is generic, T is the type of column identifier (i.e Enum or
 * String), depending on case
 */
public class SortColumn<T> {
	private final T column;
	private final boolean ascending;

	public SortColumn(T column, boolean ascending) {
		this.column = column;
		this.ascending = ascending;
	}

	public T getColumn() {
		return column;
	}

	public boolean isAscending() {
		return ascending;
	}
}

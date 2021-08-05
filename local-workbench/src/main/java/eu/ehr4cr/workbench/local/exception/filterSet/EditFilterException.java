package eu.ehr4cr.workbench.local.exception.filterSet;

public class EditFilterException extends RuntimeException {
	private final Integer filterSetId;

	public EditFilterException(Integer filterSetId, Throwable reason) {
		super("Unable to edit filterset:" + filterSetId, reason);
		this.filterSetId = filterSetId;
	}

	public Integer getFilterSetId() {
		return filterSetId;
	}
}

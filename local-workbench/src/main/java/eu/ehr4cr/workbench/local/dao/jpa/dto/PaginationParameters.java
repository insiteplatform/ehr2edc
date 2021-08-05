package eu.ehr4cr.workbench.local.dao.jpa.dto;

public class PaginationParameters {
	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE_NUMBER = 0;

	private Integer pageSize = DEFAULT_PAGE_SIZE;
	private Integer pageNr = DEFAULT_PAGE_NUMBER;

	private PaginationParameters() {
	}

	public PaginationParameters(Integer pageSize, Integer pageNr) {
		if (pageSize != null) {
			this.pageSize = pageSize;
		}
		if (pageNr != null) {
			this.pageNr = pageNr;
		}
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getPageNr() {
		return pageNr;
	}
}

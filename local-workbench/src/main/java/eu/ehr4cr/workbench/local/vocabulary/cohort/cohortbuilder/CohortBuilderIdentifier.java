package eu.ehr4cr.workbench.local.vocabulary.cohort.cohortbuilder;

import java.util.Objects;

public class CohortBuilderIdentifier {
	private final int filterSetId;

	public CohortBuilderIdentifier(int filterSetId) {
		this.filterSetId = filterSetId;
	}

	public int getFilterSetId() {
		return filterSetId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CohortBuilderIdentifier that = (CohortBuilderIdentifier) o;
		return filterSetId == that.filterSetId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(filterSetId);
	}
}
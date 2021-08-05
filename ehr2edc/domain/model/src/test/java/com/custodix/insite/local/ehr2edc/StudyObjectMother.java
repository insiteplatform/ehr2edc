package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother;

public class StudyObjectMother {

	public static Study aDefaultStudy() {
		return Study.restoreSnapshot(StudySnapshotObjectMother.aDefaultStudySnapshot());
	}
}
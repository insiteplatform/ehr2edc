package com.custodix.insite.local.ehr2edc.audit;

import java.util.ArrayList;
import java.util.List;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

final class AuditJsonComparator extends CustomComparator {
	private static final List<Customization> CUSTOMIZATIONS = ignoreVariablePathsCustomizations();

	AuditJsonComparator() {
		super(JSONCompareMode.STRICT, CUSTOMIZATIONS.toArray(new Customization[0]));
	}

	private static List<Customization> ignoreVariablePathsCustomizations() {
		List<Customization> customizations = new ArrayList<>();
		customizations.add(new Customization("eventId", { o1, o2 -> true }))
		customizations.add(new Customization("action.time.offset", { o1, o2 -> true }))
		customizations.add(new Customization("observer.entity", { o1, o2 -> true }))
		return customizations;
	}
}

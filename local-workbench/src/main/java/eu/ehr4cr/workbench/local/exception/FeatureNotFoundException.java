package eu.ehr4cr.workbench.local.exception;

import java.text.MessageFormat;

public class FeatureNotFoundException extends RuntimeException {
	public FeatureNotFoundException(String featureKey) {
		super(MessageFormat.format("No feature with key {0}", featureKey));
	}
}


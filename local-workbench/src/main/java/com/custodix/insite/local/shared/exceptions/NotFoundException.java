package com.custodix.insite.local.shared.exceptions;

import static java.lang.String.format;

import eu.ehr4cr.workbench.local.exception.feasibility.DomainException;

public class NotFoundException extends DomainException {
	public NotFoundException(Class clazz, String message) {
		super(format("Failed to find '%s' due to: '%s' ", clazz.getName(), message));
	}
}

package com.custodix.insite.local.ehr2edc.query.executor.common.projector.number;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class FormatNumber implements Projector<String, Number> {
	public static final char DEFAULT_DECIMAL_SEPARATOR = '.';
	private static final String NAME = "Format number";

	private final int maximumFractionDigits;
	private final char decimalSeparator;

	public FormatNumber(int maximumFractionDigits, Character decimalSeparator) {
		this.maximumFractionDigits = maximumFractionDigits;
		if (decimalSeparator == null) {
			this.decimalSeparator = DEFAULT_DECIMAL_SEPARATOR;
		} else {
			this.decimalSeparator = decimalSeparator;
		}
	}

	@Override
	public Optional<String> project(Optional<Number> value, ProjectionContext context) {
		return value.map(this::formatNumber);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private String formatNumber(Number number) {
		DecimalFormat decimalFormat = new DecimalFormat();
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
		formatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormat.setDecimalFormatSymbols(formatSymbols);
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMaximumFractionDigits(maximumFractionDigits);
		return decimalFormat.format(number);
	}
}

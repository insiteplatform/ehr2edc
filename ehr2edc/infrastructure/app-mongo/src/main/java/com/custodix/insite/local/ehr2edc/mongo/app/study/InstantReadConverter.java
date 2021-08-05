package com.custodix.insite.local.ehr2edc.mongo.app.study;

import java.time.Instant;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class InstantReadConverter implements Converter<Date, Instant> {
	@Override
	public Instant convert(Date date) {
		return date.toInstant();
	}
}

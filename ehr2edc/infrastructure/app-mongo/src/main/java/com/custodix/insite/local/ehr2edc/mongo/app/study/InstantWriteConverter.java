package com.custodix.insite.local.ehr2edc.mongo.app.study;

import java.time.Instant;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class InstantWriteConverter implements Converter<Instant, Date> {
	@Override
	public Date convert(Instant instant) {
		return Date.from(instant);
	}
}

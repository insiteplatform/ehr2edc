package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import java.util.List;

public  class Request {
	private final List<Integer> values;
	private final int value;

	Request(List<Integer> values, int value) {
		this.values = values;
		this.value = value;
	}

	public List<Integer> getValues() {
		return values;
	}

	public int getValue() {
		return value;
	}
}
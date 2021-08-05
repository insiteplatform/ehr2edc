package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

class SynchronizedRequest {
	@SynchronizationCorrelator
	private StudyId correlator;
	private final List<Integer> values;
	private final int value;
	private AtomicBoolean started = new AtomicBoolean(false);

	SynchronizedRequest(String correlator, List<Integer> values, int value) {
		this.correlator = StudyId.of(correlator);
		this.values = values;
		this.value = value;
	}

	public void start() {
		if (!started.compareAndSet(false, true)) {
			throw new RuntimeException("tis kapot");
		}
	}

	public void stop() {
		if (!started.compareAndSet(true, false)) {
			throw new RuntimeException("tis kapotter");
		}
	}

	public List<Integer> getValues() {
		return values;
	}

	public int getValue() {
		return value;
	}

	public Request toRequest() {
		return new Request(values, value);
	}
}

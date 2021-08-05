package com.custodix.insite.local.ehr2edc.infra.concurrency.synchronization;

import static java.util.Arrays.asList;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.util.CorrelatorLookup;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EHR2EDCSynchronizationController {

	private final Map<Object, Lock> synchronizedLocks;
	private final CorrelatorLookup correlatorLookup;

	public EHR2EDCSynchronizationController() {
		synchronizedLocks = new ConcurrentHashMap<>();
		correlatorLookup = CorrelatorLookup.ofType(SynchronizationCorrelator.class);
	}

	@Around("@within(com.custodix.insite.local.ehr2edc.shared.annotations.Command)")
	private Object synchronizeCommand(ProceedingJoinPoint pjp) throws Throwable {
		Optional<Lock> lock = optionalLock(pjp.getArgs());
		if (lock.isPresent()) {
			synchronized (lock.get()) {
				return pjp.proceed();
			}
		}
		return pjp.proceed();
	}

	private Optional<Lock> optionalLock(Object[] arguments) {
		return correlatorLookup.findAllRecursively(asList(arguments))
				.stream()
				.filter(Objects::nonNull)
				.map(correlator -> synchronizedLocks.computeIfAbsent(correlator, c -> new Lock()))
				.findFirst();
	}

	private static class Lock {
		Lock() {}
	}
}

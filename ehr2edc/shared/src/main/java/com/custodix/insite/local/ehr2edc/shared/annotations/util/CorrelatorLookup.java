package com.custodix.insite.local.ehr2edc.shared.annotations.util;

import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public final class CorrelatorLookup {

	private static final Predicate<Field> EXCLUDED_FROM_RECURSION = f -> !f.isSynthetic() && !Modifier.isStatic(
			f.getModifiers()) && !Modifier.isTransient(f.getModifiers());

	private final Class<? extends Annotation> correlatorType;

	private CorrelatorLookup(Class<? extends Annotation> correlatorType) {
		this.correlatorType = correlatorType;
	}

	public static CorrelatorLookup ofType(Class<? extends Annotation> correlatorType) {
		return new CorrelatorLookup(correlatorType);
	}

	public List<Object> findAllRecursively(Object arg) {
		if (arg instanceof Iterable) {
			return correlatorsFor((Iterable) arg);
		}
		return correlatorsFor(arg);
	}

	private List<Object> correlatorsFor(Iterable arg) {
		return StreamSupport.stream(((Iterable<Object>) arg).spliterator(), false)
				.map(this::correlatorsFor)
				.flatMap(Collection::stream)
				.collect(toList());
	}

	private List<Object> correlatorsFor(Object arg) {
		List<Object> correlators = new ArrayList<>();
		if (nonNull(arg) && !ClassUtils.isPrimitiveOrWrapper(arg.getClass())) {
			recursivelyAddAnnotatedFieldsTo(correlators).accept(arg);
		}
		return correlators.stream()
				.distinct()
				.collect(toList());
	}

	private Consumer<Object> recursivelyAddAnnotatedFieldsTo(List<Object> correlators) {
		return arg -> stream(arg.getClass().getDeclaredFields())
				.map(addAnnotatedFields(arg, correlators))
				.filter(EXCLUDED_FROM_RECURSION)
				.map(readValueFrom(arg))
				.map(this::findAllRecursively)
				.flatMap(Collection::stream)
				.forEach(correlators::add);
	}

	private Function<Field, Field> addAnnotatedFields(Object arg, List<Object> correlators) {
		return field -> {
			if (hasCorrelatorAnnotation().test(field)) {
				correlators.add(readValueFrom(arg).apply(field));
			}
			return field;
		};
	}

	private Function<Field, Object> readValueFrom(Object arg) {
		return f -> {
			try {
				return FieldUtils.readField(f, arg, true);
			} catch (IllegalAccessException e) {
				return null;
			}
		};
	}

	private Predicate<Field> hasCorrelatorAnnotation() {
		return f -> f.isAnnotationPresent(correlatorType);
	}

}
package com.custodix.insite.local.ehr2edc.security.util;

import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;

public final class AuthorizationCorrelatorUtil {

	private static final Predicate<Field> HAS_CORRELATOR_ANNOTATION = f -> f.isAnnotationPresent(
			AuthorizationCorrelator.class);
	private static final Predicate<Field> EXCLUDED_FROM_RECURSION = f -> !f.isSynthetic() && !Modifier.isStatic(
			f.getModifiers());

	private AuthorizationCorrelatorUtil() {
	}

	public static List<Object> findAllCorrelatorsRecursively(Object arg) {
		if (arg instanceof Iterable) {
			return correlatorsFor((Iterable) arg);
		}
		return correlatorsFor(arg);
	}

	private static List<Object> correlatorsFor(Iterable arg) {
		return StreamSupport.stream(((Iterable<Object>) arg).spliterator(), false)
				.map(AuthorizationCorrelatorUtil::correlatorsFor)
				.flatMap(Collection::stream)
				.collect(toList());
	}

	private static List<Object> correlatorsFor(Object arg) {
		List<Object> correlators = new ArrayList<>();
		if (nonNull(arg) && !ClassUtils.isPrimitiveOrWrapper(arg.getClass())) {
			recursivelyAddAnnotatedFieldsTo(correlators).accept(arg);
		}
		return correlators.stream()
				.distinct()
				.filter(Objects::nonNull)
				.collect(toList());
	}

	private static Consumer<Object> recursivelyAddAnnotatedFieldsTo(List<Object> correlators) {
		return arg -> stream(arg.getClass()
				.getDeclaredFields()).map(addAnnotatedFields(arg, correlators))
				.filter(EXCLUDED_FROM_RECURSION)
				.map(readValueFrom(arg))
				.map(AuthorizationCorrelatorUtil::findAllCorrelatorsRecursively)
				.flatMap(Collection::stream)
				.forEach(correlators::add);
	}

	private static Function<Field, Field> addAnnotatedFields(Object arg, List<Object> correlators) {
		return field -> {
			if (HAS_CORRELATOR_ANNOTATION.test(field)) {
				correlators.add(readValueFrom(arg).apply(field));
			}
			return field;
		};
	}

	private static Function<Field, Object> readValueFrom(Object arg) {
		return f -> {
			try {
				return FieldUtils.readField(f, arg, true);
			} catch (IllegalAccessException e) {
				return null;
			}
		};
	}

}